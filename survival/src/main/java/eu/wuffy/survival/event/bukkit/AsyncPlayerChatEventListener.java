package eu.wuffy.survival.event.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import eu.wuffy.core.handler.ChatHandler;
import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.event.EventListener;

public class AsyncPlayerChatEventListener extends EventListener {

	private final ChatHandler chatHandler;

//	private LandsIntegration lands;

	public AsyncPlayerChatEventListener(Survival core) {
		super(core);

		this.chatHandler = core.getChatHandler();
	}

	@Override
	public void onEnable() {
//		this.lands = core.getLandsIntegration();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		event.setCancelled(true);

		Bukkit.broadcastMessage(this.chatHandler.getMessagePattern(player)
				.replace("%p", player.getName())
				.replace("%m", player.hasPermission("wuffy.chat.color") ? ChatColor.translateAlternateColorCodes('&', event.getMessage()) : event.getMessage()));
	}
}