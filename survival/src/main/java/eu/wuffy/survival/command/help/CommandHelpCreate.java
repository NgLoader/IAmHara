package eu.wuffy.survival.command.help;

import java.sql.SQLException;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.help.HelpHandler;

public class CommandHelpCreate implements CommandExecutor {

	private final Survival core;
	private final HelpHandler handler;

	public CommandHelpCreate(Survival core) {
		this.core = core;
		this.handler = this.core.getHelpHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("wuffy.help.create")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}
		
		if(args.length > 1) {
			String permission = args[0].equals("-") ? "" : args[0];
			String message = String.join(" ", (String[]) ArrayUtils.remove(args, 0));
			
			try {
				this.handler.addLine(message, permission);
				sender.sendMessage(Survival.PREFIX + "§7Du hast die §aNachricht §8\"§7" + message + "§8\" §7mit dem §aRecht §8\"§7" + permission + "§8\" §7zur Help liste §ahinzugefügt§8.");
			} catch (SQLException e) {
				e.printStackTrace();
				sender.sendMessage(Survival.PREFIX + "§7Es ist ein §cFehler §7beim ausführen des commands aufgetreten§8.");
			}
			return true;
		}
		
		sender.sendMessage(Survival.PREFIX + "§7/HelpCreate §8<§7Permission§8> §8<§7Message§8>§8.");
		return true;
	}
}
