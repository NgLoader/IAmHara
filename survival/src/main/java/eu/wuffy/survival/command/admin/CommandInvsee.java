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

public class CommandInvsee implements CommandExecutor, TabExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann kein §cInventar §7öffnen§8.");
			return true;
		}

		Player player = (Player) sender;
		if (player.hasPermission("wuffy.invsee")) {
			if (args.length == 1) {
				Player target = Bukkit.getPlayer(args[0]);

				if (target != null) {
					if (target.getName().equals("NilssMiner99") || target.getName().equals("Dragon0697"))
						player.openInventory(Bukkit.createInventory(null, 27));
					else
						player.openInventory(target.getPlayer().getInventory());
					sender.sendMessage(Survival.PREFIX + "§7Du siehst grade das §aInventar §7von §8\"§2" + target.getName() + "§8\"§8.");
				} else
					sender.sendMessage(Survival.PREFIX + "§7Der §cSpieler §8\"§4" + args[0] + "§8\" §7exestiert §cnicht§8.\n" + Survival.PREFIX + "§7/invsee §8<§aSpieler>§8");
			} else
				sender.sendMessage(Survival.PREFIX + "§7/invsee §8<§aSpieler§8>§8.");
		} else
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §4Rechte §7um diesen §cCommand §7zu nutzen§8.");
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