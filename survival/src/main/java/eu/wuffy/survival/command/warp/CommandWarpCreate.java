package eu.wuffy.survival.command.warp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.warp.Warp;
import eu.wuffy.survival.warp.WarpHandler;

public class CommandWarpCreate implements CommandExecutor {

	private Survival core;

	public CommandWarpCreate(Survival core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann kein §cWarp §7erstellen§8.");
			return true;
		}
		Player player = (Player) sender;

		if (!player.hasPermission("wuffy.warp.create")) {
			player.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}

		if (args.length >= 1) {
        	WarpHandler handler = this.core.getWarpHandler();
			String warpName = args[0];
			String permission = args.length > 1 ? args[1].equalsIgnoreCase("-") ? "" : args[1] : "";
			List<String> description = new ArrayList<>();

			for (int i = 2; i < args.length; i++)
				description.add(args[i]);

			if (handler.exist(warpName.toLowerCase())) {
				player.sendMessage(Survival.PREFIX + "§7Dieser §cWarp §7exestiert schon§8.");
				return true;
			}

			Warp warp = handler.create(warpName, String.join(" ", description), permission, player.getLocation(), new ArrayList<String>());
			player.sendMessage(Survival.PREFIX + "§7Du hast erfolgreich denn §aWarp §8\"§a" + warp.name + "§8\" §aerstellt§8.");
		} else {
			player.sendMessage(Survival.PREFIX + "§7/CreateWarp §8<§7name§8> §8[§7permission§8] §8[§7Beschreibung§8]");
		}
		return true;
	}
}
