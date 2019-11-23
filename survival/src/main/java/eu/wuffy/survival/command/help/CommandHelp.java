package eu.wuffy.survival.command.help;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.help.HelpHandler;

public class CommandHelp implements CommandExecutor {

	private final Survival core;
	private final HelpHandler handler;

	public CommandHelp(Survival core) {
		this.core = core;
		this.handler = this.core.getHelpHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission("wuffy.help"))
			sender.sendMessage(this.handler.build(sender));
		else
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
		return true;
	}
}
