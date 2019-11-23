package eu.wuffy.survival.command.admin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.util.ArrayUtil;

public class CommandFly implements CommandExecutor, TabExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Survival.PREFIX + "§7Die Console kann nicht §cFliegen§8.");
				return true;
			}

			if (sender.hasPermission("wuffy.fly")) {
				Player player = (Player) sender;

				player.setAllowFlight(!player.getAllowFlight());
				player.sendMessage(Survival.PREFIX + "§7Fliegen wurden erfolgreich " + (player.getAllowFlight() ? "§aAktiviert" : "§cDeaktiviert") + "§8.");
			} else
				sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		} else if (args.length == 1) {
			if (sender.hasPermission("wuffy.fly.other")) {
				Player target = Bukkit.getPlayer(args[0]);

				if (target != null) {
					target.setAllowFlight(!target.getAllowFlight());
					target.sendMessage(Survival.PREFIX + "§7Fliegen wurden von §8\"§a" + target.getName() + "§8\" §7erfolgreich " + (target.getAllowFlight() ? "§aAktiviert" : "§cDeaktiviert") + "§8.");
					sender.sendMessage(Survival.PREFIX + "§7Fliegen wurden für §8\"§a" + target.getName() + "§8\" §7erfolgreich " + (target.getAllowFlight() ? "§aAktiviert" : "§cDeaktiviert") + "§8.");
				} else
					sender.sendMessage(Survival.PREFIX + "§7Der angebene Spieler §8\"§c" + args[0] + "§8\" §7ist nicht §cOnline§8.");
			} else
				sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}
		sender.sendMessage(Survival.PREFIX + "§7/Fly §8[§7Spieler§8]§8.");
		return true;
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			List<String> found = new ArrayList<String>();
			String search = args[0].toLowerCase();

			for (Player player : Bukkit.getOnlinePlayers()) {
				String name = player.getName();

				if (name.toLowerCase().startsWith(search) || name.toLowerCase().contains(search)) {
					found.add(name);
				}
			}
			return found;
		}

		return ArrayUtil.EMPTY_ARRAY_LIST;
	}
}