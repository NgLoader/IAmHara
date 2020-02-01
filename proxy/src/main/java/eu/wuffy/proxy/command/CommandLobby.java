package eu.wuffy.proxy.command;

import eu.wuffy.proxy.Proxy;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandLobby extends Command {

	public static final ServerInfo SERVER_INFO_BUILD = ProxyServer.getInstance().getServerInfo("lobby");

	public CommandLobby() {
		super("lobby", "wuffy.command.lobby", "hub");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new TextComponent(Proxy.PREFIX + "§7Du §7bist §7kein §cSpieler§8."));
			return;
		}
		final ProxiedPlayer player = (ProxiedPlayer) sender;
		
		if(!player.getServer().getInfo().equals(SERVER_INFO_BUILD))
			SERVER_INFO_BUILD.ping(new Callback<ServerPing>() {
				
				public void done(ServerPing serverPing, Throwable throwable) {
					if(throwable == null)
						player.connect(SERVER_INFO_BUILD);
					else
						player.sendMessage(new TextComponent(Proxy.PREFIX + "§7Der §cServer §7ist §7grade §cnicht §7ereichbar§8."));
				}
			});
		else
			player.sendMessage(new TextComponent(Proxy.PREFIX + "§7Du §7bist §7bereits §7auf §7diesen §cServer§8."));
	}
}