package eu.wuffy.survival.handler;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import eu.wuffy.core.util.GroupUtil;
import eu.wuffy.survival.Survival;
import eu.wuffy.synced.IHandler;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;

public class ScoreboardHandler extends IHandler<Survival> {

	private final Map<String, Team> teams = new HashMap<String, Team>();

	public ScoreboardHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		LuckPermsApi luckPermsApi = this.getCore().getLuckPermsApi();

		this.removeScoreboardTeams();

		while(luckPermsApi.getGroupManager().loadAllGroups().isDone());
		Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§7Loaded §a" + luckPermsApi.getGroups().size() + " §7groups§8.");

		this.reloadAllGroups();
	}

	public void onPlayerJoin(Player player, Group group) {
		if(this.teams.containsKey(group.getName())) {
			this.teams.get(group.getName()).addEntry(player.getName());
		} else
			createTeam(group).addEntry(player.getName());
	}

	public void onPlayerQuit(OfflinePlayer player) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

		Team team = scoreboard.getEntryTeam(player.getName());
		if (team != null)
			team.removeEntry(player.getName());
	}

	public Team createTeam(Group group) {
		String teamName = getTeamName(group);

		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team = scoreboard.getTeam(teamName);
		if(team == null) {
			team = scoreboard.registerNewTeam(teamName);
			team.setPrefix(ChatColor.translateAlternateColorCodes('&', GroupUtil.getGroupPrefix(group).orElseGet(() -> GroupUtil.getGroupMeta(group, "color", "§a") + group.getFriendlyName())));
			team.setColor(ChatColor.getByChar(GroupUtil.getGroupMetaSorted(group, "tablist-color", "a")));

			if (!team.getPrefix().endsWith(" "))
				team.setPrefix(team.getPrefix() + " ");
		}

		this.getCore().getChatHandler().onScoreboardTeamUpdate(team, group);

		if(!teams.containsKey(group.getName()))
			teams.put(group.getName(), team);
		return team;
	}

	public String getTeamName(Group group) {
		String teamName = "ABCEDFGHIJKLMNOPQRSTUFWXYZ".charAt(25 - group.getWeight().orElseGet(() -> 25)) + "_" + group.getName().toUpperCase();
		if(teamName.length() > 16)
			teamName = teamName.substring(0, 16);
		return teamName;
	}

	public void reloadAllGroups() {
		this.removeScoreboardTeams();

		LuckPermsApi luckPermsApi = this.getCore().getLuckPermsApi();
		for(Group group : luckPermsApi.getGroups())
			this.createTeam(group);

		Bukkit.getOnlinePlayers().forEach(player -> this.onPlayerJoin(player, luckPermsApi.getGroup(luckPermsApi.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup())));
	}

	public void removeScoreboardTeams() {
		teams.clear();
		for(Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams())
			team.unregister();
	}
}