package eu.wuffy.core.scoreboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;

import eu.wuffy.core.Core;
import eu.wuffy.core.database.CoreDatabase;
import eu.wuffy.core.util.GroupUtil;
import eu.wuffy.synced.ICore;
import eu.wuffy.synced.IHandler;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.UserManager;
import net.minecraft.server.v1_15_R1.ChatComponentText;
import net.minecraft.server.v1_15_R1.EnumChatFormat;
import net.minecraft.server.v1_15_R1.IScoreboardCriteria;
import net.minecraft.server.v1_15_R1.IScoreboardCriteria.EnumScoreboardHealthDisplay;
import net.minecraft.server.v1_15_R1.Scoreboard;
import net.minecraft.server.v1_15_R1.ScoreboardObjective;
import net.minecraft.server.v1_15_R1.ScoreboardScore;
import net.minecraft.server.v1_15_R1.ScoreboardTeam;
import net.minecraft.server.v1_15_R1.ScoreboardTeamBase.EnumNameTagVisibility;
import net.minecraft.server.v1_15_R1.ScoreboardTeamBase.EnumTeamPush;

public class ScoreboardHandler extends IHandler<Core<?>> {

	private Scoreboard scoreboard;
	private CraftScoreboard craftScoreboard;
	private ScoreboardObjective objective;

	private Map<Player, PlayerScoreboard> playerScoreboards = new HashMap<>();
	private Map<Group, SurvivalTeam> teamScoreboardsByGroup = new HashMap<>();
	private Map<String, SurvivalTeam> teamScoreboardsByName = new HashMap<>();

	public ScoreboardHandler(Core<? extends CoreDatabase> core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.scoreboard = new Scoreboard() {

			@Override
			public void handleTeamChanged(ScoreboardTeam var0) { }

			@Override
			public void handleTeamAdded(ScoreboardTeam var0) { }

			@Override
			public void handleTeamRemoved(ScoreboardTeam var0) { }

			@Override
			public void handleObjectiveAdded(ScoreboardObjective var0) { }

			@Override
			public void handleObjectiveChanged(ScoreboardObjective var0) { }

			@Override
			public void handleObjectiveRemoved(ScoreboardObjective var0) { }

			@Override
			public void handlePlayerRemoved(String var0) { }

			@Override
			public void handleScoreChanged(ScoreboardScore var0) { }
		};

		this.craftScoreboard = (CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard();
		this.objective = new ScoreboardObjective(((CraftScoreboard) this.craftScoreboard).getHandle(),
				"abc",
				IScoreboardCriteria.DUMMY,
				new ChatComponentText("§2I§aam§2Ha§aRa§7.§2de"),
				EnumScoreboardHealthDisplay.INTEGER);

		this.craftScoreboard.getObjectives().forEach(objective -> objective.unregister());

		LuckPerms luckPerms = LuckPermsProvider.get();

		while(luckPerms.getGroupManager().loadAllGroups().isDone());
		Bukkit.getConsoleSender().sendMessage(ICore.PREFIX + "§7Loaded §a" + luckPerms.getGroupManager().getLoadedGroups().size() + " §7groups§8.");

		this.updateGroups();
	}

	@Override
	public void onDisable() {
		this.removeGroups();
	}

	public PlayerScoreboard getPlayerScoreboard(Player player) {
		PlayerScoreboard scoreboard = this.playerScoreboards.get(player);

		if (scoreboard == null) {
			scoreboard = new PlayerScoreboard(this, player, this.objective);
			this.playerScoreboards.put(player, scoreboard);
		}

		return scoreboard;
	}

	public PlayerScoreboard removePlayerScoreboard(Player player) {
		PlayerScoreboard playerScoreboard = this.playerScoreboards.remove(player);

		if (playerScoreboard != null) {
			playerScoreboard.leaveTeam(true);
			playerScoreboard.removeAllObjectives(false);
		}

		return playerScoreboard;
	}

	public SurvivalTeam getTeam(String group) {
		return this.teamScoreboardsByName.get(group);
	}

	public SurvivalTeam getTeam(Group group) {
		return this.teamScoreboardsByGroup.get(group);
	}

	public SurvivalTeam createTeam(Group group) {
		SurvivalTeam team = this.teamScoreboardsByGroup.get(group);

		if (team == null) {
			String prefix = ChatColor.translateAlternateColorCodes('&', GroupUtil.getGroupPrefix(group).orElseGet(() -> GroupUtil.getGroupMeta(group, "color", "§a") + group.getFriendlyName()));

			if (!prefix.endsWith(" ")) {
				prefix += " ";
			}

			EnumChatFormat color = EnumChatFormat.GRAY;
			String colorCode = GroupUtil.getGroupMetaSorted(group, "tablist-color", "a");

			for (EnumChatFormat format : EnumChatFormat.values()) {
				if (format.character == colorCode.charAt(0)) {
					color = format;
				}
			}

			ScoreboardTeam scoreboardTeam = new ScoreboardTeam(this.scoreboard, this.getTeamName(group));
			scoreboardTeam.setPrefix(new ChatComponentText(prefix));
			scoreboardTeam.setAllowFriendlyFire(true);
			scoreboardTeam.setCollisionRule(EnumTeamPush.ALWAYS);
			scoreboardTeam.setCanSeeFriendlyInvisibles(false);
			scoreboardTeam.setDeathMessageVisibility(EnumNameTagVisibility.NEVER);
			scoreboardTeam.setColor(color);

			team = new SurvivalTeam(scoreboardTeam);

			this.teamScoreboardsByGroup.put(group, team);
			this.teamScoreboardsByName.put(group.getName(), team);
		}

		return team;
	}

	public String getTeamName(Group group) {
		String teamName = "§" + "ABCEDFGHIJKLMNOPQRSTUFWXYZ".charAt(25 - group.getWeight().orElseGet(() -> 25)) + "_" + group.getName().toUpperCase();
		if(teamName.length() > 16)
			teamName = teamName.substring(0, 16);
		return teamName;
	}

	public void loadGroups() {
		for(Group group : LuckPermsProvider.get().getGroupManager().getLoadedGroups()) {
			this.createTeam(group);
		}
	}

	public void updateGroups() {
		UserManager userManager = LuckPermsProvider.get().getUserManager();

		this.removeGroups();
		this.loadGroups();

		for (Player player : Bukkit.getOnlinePlayers()) {
			PlayerScoreboard playerScoreboard = this.getPlayerScoreboard(player);
			userManager.loadUser(playerScoreboard.getPlayer().getUniqueId()).thenAccept(user -> {
				playerScoreboard.joinTeam(user.getPrimaryGroup());
			});
		}
	}

	public void removeGroups() {
		for (PlayerScoreboard playerScoreboard : this.playerScoreboards.values()) {
			playerScoreboard.leaveTeam(false);
		}

		for (SurvivalTeam team : this.teamScoreboardsByGroup.values()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				team.sendRemovePacket(player);
			}
		}

		this.teamScoreboardsByGroup.clear();
		this.teamScoreboardsByName.clear();
	}

	public Collection<SurvivalTeam> getTeams() {
		return this.teamScoreboardsByGroup.values();
	}

	public ScoreboardObjective getObjective() {
		return this.objective;
	}
}