package eu.wuffy.proxy.event;

import eu.wuffy.proxy.Proxy;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerSwitchEventListener implements Listener {

	@EventHandler(priority = 0)
	public void onServerSwitch(ServerSwitchEvent event) {
		ProxiedPlayer player = event.getPlayer();
		String serverName = player.getServer().getInfo().getName();

		player.sendMessage(new TextComponent(
				Proxy.PREFIX + "§7Du §7betrittst §7nun §7denn §7Server §a" + serverName.substring(0, 1).toUpperCase() + serverName.substring(1).toLowerCase() + "§8.")
			);
	}
}