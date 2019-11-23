package eu.wuffy.survival.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import eu.wuffy.survival.Survival;

public class AsyncPlayerChatEventListener implements Listener {

	private final Survival core;

	public AsyncPlayerChatEventListener(Survival core) {
		this.core = core;
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		event.setCancelled(true);

		Bukkit.broadcastMessage(player.getCustomName() + "§8» §7" + (player.hasPermission("wuffy.chat.color") ? ChatColor.translateAlternateColorCodes('&', event.getMessage()) : event.getMessage()));
	}

	public Survival getCore() {
		return this.core;
	}
}