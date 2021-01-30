package eu.wuffy.proxy.command;

import eu.wuffy.proxy.Proxy;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandFind extends Command {

	public CommandFind() {
		super("find", "wuffy.command.find", "search");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length != 1)
			sender.sendMessage(new TextComponent(Proxy.PREFIX + "§7Bitte §7gebe §7einen §cSpieler §7an§8."));
		else {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
			
			if(player != null&& player.getServer() != null) {
				sender.sendMessage(new ComponentBuilder(Proxy.PREFIX + "§7Der §aSpieler §8\"§a" + player.getName() + "§8\" §7ist §aOnline §7auf ")
						.append(player.getServer().getInfo().getName())
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aKlick zum verbinden")))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + player.getServer().getInfo().getName())).create());
			}
			else
				sender.sendMessage(new TextComponent(Proxy.PREFIX + "§7Der §cSpieler §8\"§c" + args[0] + "§8\" §7ist §7nicht §cOnline§8."));
		}
	}
}