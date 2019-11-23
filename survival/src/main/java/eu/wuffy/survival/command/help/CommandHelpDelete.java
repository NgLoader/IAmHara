package eu.wuffy.survival.command.help;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.help.HelpHandler;
import eu.wuffy.survival.handler.help.HelpLine;
import eu.wuffy.synced.util.ArrayUtil;

public class CommandHelpDelete implements CommandExecutor, TabExecutor {

	private final Survival core;
	private final HelpHandler handler;

	public CommandHelpDelete(Survival core) {
		this.core = core;
		this.handler = this.core.getHelpHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("wuffy.help.delete")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}
		
		if(args.length > 0) {
			if (args[0].matches("\\d+")) {
				int line = Integer.parseInt(args[0]);

				HelpLine helpLine = this.handler.getLine(line);
				
				if(helpLine != null) {
					try {
						this.handler.removeLine(line);
						sender.sendMessage(Survival.PREFIX + "§7Du hast die §aNachricht §8\"§7" + helpLine.message + "§8\" §7erfolgreich §cgelöscht§8.");
					} catch (SQLException e) {
						e.printStackTrace();
						sender.sendMessage(Survival.PREFIX + "§7Es ist ein §cFehler §7beim ausführen des commands aufgetreten§8.");
					}
				} else
					sender.sendMessage(Survival.PREFIX + "§7Die angegebene §cNummer §8\"§c" + line + "§8\" §7exestiert §cnicht§8.");
			} else
				sender.sendMessage(Survival.PREFIX + "§7§8\"§c" + args[0] + "§8\" §7ist keine valide §cZahl§8.");
			return true;
		}

		sender.sendMessage(Survival.PREFIX + "§7/HelpDelete §7<§8Line§7>§8.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1)
			return this.handler.getLines().stream().map(line -> String.valueOf(line.line)).collect(Collectors.toList());
		return ArrayUtil.EMPTY_ARRAY_LIST;
	}
}
