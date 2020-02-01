package eu.wuffy.core.scoreboard;

import java.util.UUID;

import org.bukkit.entity.Player;

import eu.wuffy.core.util.NMSUtil;
import net.minecraft.server.v1_15_R1.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_15_R1.ScoreboardObjective;
import net.minecraft.server.v1_15_R1.ScoreboardServer.Action;

public class SurvivalObjective {

	private final ScoreboardObjective objective;

	public final Player player;
	public final UUID uuid;
	public final String identifier;
	private String scoreValue;
	private int score;

	public SurvivalObjective(ScoreboardObjective objective, Player player, String identifier, String scoreValue, int score) {
		this.objective = objective;
		this.player = player;
		this.identifier = identifier;
		this.scoreValue = scoreValue;
		this.score = score;

		this.uuid = this.player.getUniqueId();
	}

	public void updateScoreValue(String scoreValue) {
		this.remove();
		this.scoreValue = scoreValue;
		this.update();
	}

	public void updateScore(int score) {
		this.remove();
		this.score = score;
		this.update();
	}

	public void update() {
		PacketPlayOutScoreboardScore packetPlayOutScoreboardScore = new PacketPlayOutScoreboardScore(Action.CHANGE, this.objective.getName(), this.scoreValue, this.score);
		NMSUtil.sendPacket(player, packetPlayOutScoreboardScore);
	}

	public void remove() {
		PacketPlayOutScoreboardScore packetPlayOutScoreboardScore = new PacketPlayOutScoreboardScore(Action.REMOVE, this.objective.getName(), this.scoreValue, this.score);
		NMSUtil.sendPacket(player, packetPlayOutScoreboardScore);
	}

	public String getScoreValue() {
		return this.scoreValue;
	}

	public int getScore() {
		return this.score;
	}
}