package eu.wuffy.survival.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.ChatHandler;

public class AsyncPlayerChatEventListener implements Listener {

	private final Survival core;
	private final ChatHandler chatHandler;

	public AsyncPlayerChatEventListener(Survival core) {
		this.core = core;
		this.chatHandler = this.core.getChatHandler();
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		event.setCancelled(true);

		Team team = player.getScoreboard().getEntryTeam(player.getName());

		if(team != null) {
			Bukkit.broadcastMessage(this.chatHandler.getChatPrefix(team)
					.replace("%p", player.getDisplayName())
					.replace("%m", player.hasPermission("wuffy.chat.color") ? ChatColor.translateAlternateColorCodes('&', event.getMessage()) : event.getMessage()));
			return;
		}

		event.getPlayer().sendMessage(Survival.PREFIX + "§7Leider ist ein §cFehler §7aufgetreten§8.");
	}

	public Survival getCore() {
		return this.core;
	}
}