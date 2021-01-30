package eu.wuffy.core.scoreboard;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import eu.wuffy.core.util.PlayerUtil;
import net.luckperms.api.model.group.Group;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_16_R3.ScoreboardObjective;

public class PlayerScoreboard {

	private final ScoreboardHandler scoreboardHandler;
	private final Player player;

	private ScoreboardObjective objective;
	private Map<String, SurvivalObjective> objectiveByIdentifier = new HashMap<String, SurvivalObjective>();

	private SurvivalTeam currentlyTeam;

	public PlayerScoreboard(ScoreboardHandler scoreboardHandler, Player player, ScoreboardObjective objective) {
		this.scoreboardHandler = scoreboardHandler;
		this.player = player;
		this.objective = objective;
	}

	public void joinTeam(String group) {
		this.joinTeam(this.scoreboardHandler.getTeam(group));
	}

	public void joinTeam(Group group) {
		this.joinTeam(this.scoreboardHandler.getTeam(group));
	}

	public void joinTeam(SurvivalTeam team) {
		List<String> entry = Arrays.asList(this.player.getName());

		if (currentlyTeam != null) {
			currentlyTeam.removeEntry(entry);
		}

		this.currentlyTeam = team;
		team.addEntry(entry);
	}

	public void leaveTeam(boolean sendLeavePacket) {
		if (this.currentlyTeam != null) {
			if (sendLeavePacket) {
				this.currentlyTeam.removeEntry(Arrays.asList(this.player.getName()));
			}

			this.currentlyTeam = null;
		}
	}

	public void createObjective() {
		PacketPlayOutScoreboardObjective packetPlayOutScoreboardObjectiveDelete = new PacketPlayOutScoreboardObjective(this.objective, 1);
		PacketPlayOutScoreboardObjective packetPlayOutScoreboardObjectiveCreate = new PacketPlayOutScoreboardObjective(this.objective, 0);
		PacketPlayOutScoreboardDisplayObjective packetPlayOutScoreboardDisplayObjective = new PacketPlayOutScoreboardDisplayObjective(1, this.objective);

		PlayerUtil.sendPacket(this.player, packetPlayOutScoreboardObjectiveDelete, packetPlayOutScoreboardObjectiveCreate, packetPlayOutScoreboardDisplayObjective);
	}

	public SurvivalObjective createObjective(String identifier, String scoreValue, int score) {
		SurvivalObjective objective = getObjective(identifier);

		if (objective != null) {
			this.removeObjective(objective);
		}

		this.addObjective(objective = new SurvivalObjective(this.objective, this.player, identifier, scoreValue, score), identifier);
		return objective;
	}

	public void reScoreAll() {
		int currentScore = 0;

		for(SurvivalObjective objective : this.objectiveByIdentifier.values().stream()
				.sorted(Comparator.comparingInt(SurvivalObjective::getScore))
				.collect(Collectors.toList())) {
			objective.updateScore(currentScore++);
		}
	}

	public SurvivalObjective addObjective(SurvivalObjective objective) {
		if (objective == null) {
			return null;
		}

		return this.addObjective(objective, objective.identifier);
	}

	public SurvivalObjective addObjective(SurvivalObjective objective, String identifier) {
		this.objectiveByIdentifier.put(identifier, objective);
		objective.update();

		return objective;
	}

	public SurvivalObjective getObjective(String identifier) {
		return this.objectiveByIdentifier.get(identifier);
	}

	public boolean hasObjective(String identifier) {
		return this.objectiveByIdentifier.containsKey(identifier);
	}

	public void removeObjective(String identifier) {
		SurvivalObjective objective = this.objectiveByIdentifier.remove(identifier);

		if (objective != null) {
			objective.remove();
		}
	}

	public void removeObjective(SurvivalObjective objective) {
		this.objectiveByIdentifier.remove(objective.identifier);
		objective.remove();
	}

	public void removeAllObjectives(boolean sendPacket) {
		if (sendPacket) {
			this.objectiveByIdentifier.values().forEach(objective -> objective.remove());
		}

		this.objectiveByIdentifier.clear();
	}

	public SurvivalTeam getCurrentlyTeam() {
		return this.currentlyTeam;
	}

	public Player getPlayer() {
		return this.player;
	}

	public ScoreboardHandler getScoreboardHandler() {
		return this.scoreboardHandler;
	}
}