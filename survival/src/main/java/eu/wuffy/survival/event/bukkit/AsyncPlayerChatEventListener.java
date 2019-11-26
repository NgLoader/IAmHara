package eu.wuffy.survival.event.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.event.EventListener;

public class AsyncPlayerChatEventListener extends EventListener {

	public AsyncPlayerChatEventListener(Survival core) {
		super(core);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		event.setCancelled(true);

		Bukkit.broadcastMessage(player.getCustomName() + "§8» §7" + (player.hasPermission("wuffy.chat.color") ? ChatColor.translateAlternateColorCodes('&', event.getMessage()) : event.getMessage()));
	}
}