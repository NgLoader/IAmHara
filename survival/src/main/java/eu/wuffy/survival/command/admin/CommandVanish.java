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
import eu.wuffy.survival.handler.VanishHandler;
import eu.wuffy.synced.util.ArrayUtil;

public class CommandVanish implements CommandExecutor, TabExecutor {

	private final VanishHandler handler;

	public CommandVanish(Survival core) {
		this.handler = core.getVanishHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.hasPermission("wuffy.vanish")) {
			if (args.length > 0) {
				if(!sender.hasPermission("wuffy.vanish.other")) {
					sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
					return true;
				}
				Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : null;

				if (target == null) {
					sender.sendMessage(Survival.PREFIX + "§7Der angegebene §aSpieler §7ist nicht online§8.");
					return true;
				}

				if (target.equals(sender)) {
						sender.sendMessage(Survival.PREFIX + "§7Du kannst dich nicht selbst als §cziel §7auswählen§8.");
					return true;
				}

				if (this.handler.togglePlayer(target)) {
					sender.sendMessage(Survival.PREFIX + "§7Der Spieler §a" + target.getName() + "§7 ist nun im §avanish §7modus§8.");
					target.sendMessage(Survival.PREFIX + "§7Du bist nun §aunsichtbar§8.");
				} else {
					sender.sendMessage(Survival.PREFIX + "§7Der Spieler §a" + target.getName() + "§7 ist nun §cnicht §7mehr im §avanish §7modus§8.");
					target.sendMessage(Survival.PREFIX + "§7Du bist nun §cnicht §7mehr §cunsichtbar§8.");
				}
				return true;
			}

			if(!(sender instanceof Player)) {
				sender.sendMessage(Survival.PREFIX + "§7Die Console kann sich nicht §cunsichtbar §7machen§8.");
				return true;
			}
			Player player = (Player) sender;

			if (this.handler.togglePlayer(player))
				sender.sendMessage(Survival.PREFIX + "§7Du bist nun §aunsichtbar§8.");
			else
				sender.sendMessage(Survival.PREFIX + "§7Du bist nun §cnicht §7mehr §cunsichtbar§8.");
		} else
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1) {
			List<String> found = new ArrayList<String>();
			String search = args[0].toLowerCase();

			for (Player player : Bukkit.getOnlinePlayers()) {
				String name = player.getName();

				if (!sender.equals(player) && (name.toLowerCase().startsWith(search) || name.toLowerCase().contains(search))) {
					found.add(name);
				}
			}
			return found;
		}

		return ArrayUtil.EMPTY_ARRAY_LIST;
	}
}