package eu.wuffy.survival.command.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.warp.Warp;
import eu.wuffy.survival.warp.WarpHandler;

public class CommandWarpDelete implements CommandExecutor {

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
}
