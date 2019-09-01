package eu.wuffy.survival.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import eu.wuffy.core.util.RankUtil;
import eu.wuffy.survival.Survival;
import eu.wuffy.synced.Rank;

public class ScoreboardHandler {

	private final Survival core;
	private final Map<Rank, Team> teams = new HashMap<Rank, Team>();

	public ScoreboardHandler(Survival core) {
		this.core = core;
	}
	
	public void init() {
		this.removeAllScoreboards();

		for(Rank rank : Rank.values())
			this.createTeam(rank);

		Bukkit.getOnlinePlayers().forEach(player -> addPlayerToScoreboard(player, RankUtil.getRankByPermission(player)));
	}
	
	public void reloadScoreboard() {
		removeAllScoreboards();
		Bukkit.getOnlinePlayers().forEach(player -> addPlayerToScoreboard(player, RankUtil.getRankByPermission(player)));
	}
	
	public void addPlayerToScoreboard(Player player, Rank rank) {
		if(teams.containsKey(rank))
			teams.get(rank).addEntry(player.getName());
		else
			createTeam(rank).addEntry(player.getName());
	}
	
	public void removePlayerFromScoreboard(Player player) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

		Team team = scoreboard.getEntryTeam(player.getName());
		if (team != null)
			team.removeEntry(player.getName());
	}
	
	public void addAllPlayersToScoreboard(List<Player> players) {
		players.forEach(player -> addPlayerToScoreboard(player, RankUtil.getRankByPermission(player)));
	}
	
	public void removeAllScoreboards() {
		teams.clear();
		for(Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams())
			team.unregister();
	}
	
	public Team createTeam(Rank rank) {
		String teamName = getTeamName(rank);
		
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team team = scoreboard.getTeam(teamName);
		if(team == null) {
			team = scoreboard.registerNewTeam(teamName);
			String prefix = rank.getColorCode() + rank.getDisplayName() + " §8» ";
			int i = rank.getDisplayName().length();
			
			while(prefix.length() > 16)
				prefix = rank.getColorCode() + rank.getDisplayName().substring(0, (i -= 1)) + " §8» ";
			
			team.setPrefix(prefix);
			team.setColor(ChatColor.GRAY);
		}
		if(!teams.containsKey(rank))
			teams.put(rank, team);
		return team;
	}
	
	public String getTeamName(Rank rank) {
		String teamName = "ABCEDFGHIJKLMNOPQRSTUFWXYZ".charAt(rank.ordinal()) + "_" + rank.getFullDisplayName().toUpperCase();
		if(teamName.length() > 16)
			teamName = teamName.substring(0, 16);
		return teamName;
	}

	public Survival getCore() {
		return this.core;
	}
}