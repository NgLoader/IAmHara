package eu.wuffy.survival.command.help;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.help.HelpHandler;
import eu.wuffy.survival.help.HelpLine;
import eu.wuffy.synced.util.ArrayUtil;

public class CommandHelpUpdate implements CommandExecutor, TabExecutor {

	private final Survival core;
	private final HelpHandler handler;

	public CommandHelpUpdate(Survival core) {
		this.core = core;
		this.handler = this.core.getHelpHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.hasPermission("wuffy.help.update")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}
		
		if(args.length > 2) {
			int line;
			try {
				line = Integer.parseInt(args[0]);
			} catch(NumberFormatException ex) {
				sender.sendMessage(Survival.PREFIX + "§7§8\"§c" + args[0] + "§8\" §7ist keine valide §cZahl§8.");
				return true;
			}
			HelpLine message = this.handler.getLine(line);
			
			if(message != null) {
				String permission = args[1].equals("*") ? null : args[1].equals("-") ? null : args[1];
				String newMessage = String.join(" ", (String[]) ArrayUtils.remove(ArrayUtils.remove(args, 0), 0));

				try {
					this.handler.setLine(line, newMessage, permission != null ? permission : "");
					sender.sendMessage(Survival.PREFIX + "§7Du hast die §aNachricht §8\"§7" + message.message + "§8\" §7erfolgreich §aAktualisiert§8.");
				} catch (SQLException e) {
					e.printStackTrace();
					sender.sendMessage(Survival.PREFIX + "§7Es ist ein §cFehler §7beim ausführen des commands aufgetreten§8.");
				}
			} else
				sender.sendMessage(Survival.PREFIX + "§7Die angegebene §cNummer §8\"§c" + line + "§8\" §7exestiert §cnicht§8.");
			return true;
		}
		
		sender.sendMessage(Survival.PREFIX + "§7/HelpUpdate §7<§8Line§7> §8<§7Permission§8> §7<§8NeueMessage§7>§8.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1)
			return this.handler.getLines().stream().map(line -> String.valueOf(line.line)).collect(Collectors.toList());
		else if (args.length == 2 && args[0].matches("\\d+")) {
			HelpLine helpLine = this.handler.getLine(Integer.valueOf(args[0]));

			if (helpLine != null)
				return Collections.singletonList(helpLine.permission.isEmpty() ? "-" : helpLine.permission);
		}
		return ArrayUtil.EMPTY_ARRAY_LIST;
	}
}
