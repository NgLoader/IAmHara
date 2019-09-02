package eu.wuffy.survival.handler;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import eu.wuffy.core.util.GroupUtil;
import eu.wuffy.survival.Survival;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.LuckPermsApi;

public class ScoreboardHandler {

	private final Survival core;
	private final Map<String, Team> teams = new HashMap<String, Team>();

	public ScoreboardHandler(Survival core) {
		this.core = core;
	}

	public void init() {
		this.removeAllScoreboards();
		LuckPermsApi luckPermsApi = this.core.getLuckPermsApi();

		for(Group group : luckPermsApi.getGroups())
			this.createTeam(group);

		Bukkit.getOnlinePlayers().forEach(player -> addPlayerToScoreboard(player, luckPermsApi.getGroup(luckPermsApi.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup())));
	}

	public void addPlayerToScoreboard(Player player, Group group) {
		if(teams.containsKey(group.getName()))
			teams.get(group.getName()).addEntry(player.getName());
		else
			createTeam(group).addEntry(player.getName());
	}

	public void removePlayerFromScoreboard(Player player) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

		Team team = scoreboard.getEntryTeam(player.getName());
		if (team != null)
			team.removeEntry(player.getName());
	}

	public void removeAllScoreboards() {
		teams.clear();
		for(Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams())
			team.unregister();
	}

	public Team createTeam(Group group) {
		String teamName = getTeamName(group);

		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team = scoreboard.getTeam(teamName);
		if(team == null) {
			team = scoreboard.registerNewTeam(teamName);
			team.setPrefix(ChatColor.translateAlternateColorCodes('&', GroupUtil.getGroupPrefix(group).orElseGet(() -> GroupUtil.getGroupMeta(group, "color", "§a") + group.getFriendlyName())));
			team.setColor(ChatColor.getByChar(GroupUtil.getGroupMeta(group, "color", "a")));

			if (!team.getPrefix().endsWith(" "))
				team.setPrefix(team.getPrefix() + " ");
		}
		if(!teams.containsKey(group.getName()))
			teams.put(group.getName(), team);
		return team;
	}

	public String getTeamName(Group group) {
		String teamName = "ABCEDFGHIJKLMNOPQRSTUFWXYZ".charAt(group.getWeight().orElseGet(() -> 23)) + "_" + group.getFriendlyName().toUpperCase();
		if(teamName.length() > 16)
			teamName = teamName.substring(0, 16);
		return teamName;
	}

	public Survival getCore() {
		return this.core;
	}
}