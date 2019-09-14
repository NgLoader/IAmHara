package eu.wuffy.survival.command.warp;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.warp.Warp;
import eu.wuffy.survival.warp.WarpHandler;
import eu.wuffy.synced.util.ArrayUtil;

public class CommandWarpDelete implements CommandExecutor, TabExecutor {

	private Survival core;

	public CommandWarpDelete(Survival core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("wuffy.warp.delete")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}

		if (args.length == 1) {
			WarpHandler handler = this.core.getWarpHandler();
			String warpName = args[0].toLowerCase();

			if (!handler.exist(warpName)) {
				sender.sendMessage(Survival.PREFIX + "§7Dieser §cWarp §7exestiert nicht§8.");
				return true;
			}

			Warp warp = handler.get(warpName);
			handler.delete(warp);

			sender.sendMessage(Survival.PREFIX + "§7Du hast erfolgreich denn §aWarp §8\"§a" + warp.name + "§8\" §cgelöscht§8.");
		} else {
			sender.sendMessage(Survival.PREFIX + "§7/DeleteWarp §8<§7name§8>");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return this.core.getWarpHandler().getWarps().stream()
					.map(warp -> warp.name)
					.collect(Collectors.toList());
		} else if (args.length == 1) {
			String search = args[0].toLowerCase();

			return this.core.getWarpHandler().getWarps().stream()
					.map(warp -> warp.name)
					.filter(warp -> warp.toLowerCase().startsWith(search))
					.collect(Collectors.toList());
		}
		return ArrayUtil.EMPTY_ARRAY_LIST;
	}
}
