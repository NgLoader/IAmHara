package eu.wuffy.survival.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.Rank;

public class AsyncPlayerChatEventListener implements Listener {

	private static final String PLAYER_DEFAULT_TAG = Rank.PLAYER.getColorCode() + Rank.PLAYER.getDisplayName();

	private Survival core;

	public AsyncPlayerChatEventListener(Survival core) {
		this.core = core;
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		event.setCancelled(true);

		StringBuilder message = null;
		Team team = player.getScoreboard().getEntryTeam(player.getName());

		if(team == null)
			message = new StringBuilder(String.format("§8[%s§8] ", AsyncPlayerChatEventListener.PLAYER_DEFAULT_TAG));
		else
			message = new StringBuilder("§8[").append(team.getPrefix().substring(0, team.getPrefix().length() - 5)).append("§8] ");

		message.append(String.format("§7%s §8» §7", player.getDisplayName()));
		message.append(player.hasPermission("wuffy.chat.color") ? ChatColor.translateAlternateColorCodes('&', event.getMessage()) : event.getMessage());

		Bukkit.broadcastMessage(message.toString());
	}

	public Survival getCore() {
		return this.core;
	}
}