package eu.wuffy.proxy.command;

import eu.wuffy.proxy.Proxy;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandMsg extends Command {

	public CommandMsg() {
		super("msg", "wuffy.command.msg");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length > 1) {
			ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
			
			if(target != null)
				if(target != sender) {
					String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args).substring(args[0].length() + (args.length > 1 ? 1 : 0)));
					String senderName = (sender instanceof ProxiedPlayer ? "§a" : "§c") + sender.getName();
					
					sender.sendMessage(new ComponentBuilder("§8[§cMsg§8] §7Du §8§l▶ §a" + target.getName() + " §8» §7" + message)
							.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + target.getName() + " "))
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Schreibe §7eine §7weitere §aNachricht §7an §a" + target.getName()).create())).create());
					
					ComponentBuilder componentBuilder = new ComponentBuilder("§8[§cMsg§8] " + senderName + " §8» §7" + message);
					
					if(sender instanceof ProxiedPlayer)
						componentBuilder
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Antworte §a" + sender.getName()).create()))
						.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + sender.getName() + " "));
					else
						componentBuilder
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Du §7kannst §7der §cConsole §7nicht §cantworten").create()));
					
					target.sendMessage(componentBuilder.create());
				} else
					sender.sendMessage(new TextComponent(Proxy.PREFIX + "§7Du §7kannst §7dir §7nicht §7selber §7eine §cNachricht §7schreiben§8."));
			else
				sender.sendMessage(new TextComponent(Proxy.PREFIX + "§7Der §7Spieler §8\"§c" + args[0] + "§8\" §7ist §7nicht §cOnline§8."));
			return;
		}
		sender.sendMessage(new TextComponent(Proxy.PREFIX + "§7Bitte §7gebe §7eine §cSpieler §7und §7eine §cNachricht §7an§8."));
	}
}