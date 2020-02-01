package eu.wuffy.proxy.command;

import eu.wuffy.proxy.Proxy;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CommandAlert extends Command {

	public CommandAlert() {
		super("alert", "wuffy.command.alert");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0)
			sender.sendMessage(new TextComponent(Proxy.PREFIX + "§7Du §7musst §7eine §cNachricht §7angeben§8."));
		else
			ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(Proxy.PREFIX + ChatColor.translateAlternateColorCodes('&', String.join(" ", args))));
	}
}