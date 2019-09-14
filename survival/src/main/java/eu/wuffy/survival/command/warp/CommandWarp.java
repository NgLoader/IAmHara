package eu.wuffy.survival.command.warp;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.warp.Warp;
import eu.wuffy.synced.util.ArrayUtil;

public class CommandWarp implements CommandExecutor, TabExecutor {

	private Survival core;

	public CommandWarp(Survival core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann keine §cWarp §7nutzen§8.");
			return true;
		}
		Player player = (Player) sender;

		if (args.length == 1) {
			Warp warp = this.core.getWarpHandler().get(args[0].toLowerCase());

			if (warp == null) {
				player.sendMessage(Survival.PREFIX + "§7Dieser §cWarp §7exestiert nicht§8.");
				return true;
			}

			if (!warp.permission.equals("")) {
				if (!player.hasPermission(warp.permission)) {
					sender.sendMessage(Survival.PREFIX + "§7Du hast keine §4Rechte §7um diesen §cWarp §7zu nutzen§8.");
					return true;
				}
			} else if (!player.hasPermission("wuffy.warp.default")) {
				sender.sendMessage(Survival.PREFIX + "§7Du hast keine §4Rechte §7um diesen §cWarp §7zu nutzen§8.");
				return true;
			}

			player.teleport(warp.location);
			player.sendMessage(Survival.PREFIX + "§7Du wurdest §aerfolgreich §7zum Warp §8\"§a" + warp.name + "§8\" §7teleportiert§8.");
		} else {
			player.sendMessage(Survival.PREFIX + "§7/Warp §8<§7name§8>");
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
