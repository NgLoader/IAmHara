package eu.wuffy.proxy.command;

import eu.wuffy.proxy.Proxy;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

public class CommandGlobalList extends Command {

	public CommandGlobalList() {
		super("glist", "wuffy.command.globallist", "globallist", "gl");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		sender.sendMessage(new TextComponent(Proxy.PREFIX + "§7Derzeitig §aOnline§8: " + ProxyServer.getInstance().getOnlineCount()));
		for(ServerInfo serverInfo : ProxyServer.getInstance().getServers().values())
			if(!serverInfo.canAccess(sender))
				continue;
			else {
				TextComponent component = new TextComponent(Proxy.PREFIX + "     §8- §a" + serverInfo.getName() + " §8(§7" + serverInfo.getPlayers().size() + "§8)");
				component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("§aKlicke zum verbinden").create()));
				component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + serverInfo.getName()));
				sender.sendMessage(component);
			}
	}
}