package eu.wuffy.survival.command.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.warp.Warp;
import eu.wuffy.survival.warp.WarpHandler;

public class CommandWarpCreateAlias implements CommandExecutor {

	private Survival core;

	public CommandWarpCreateAlias(Survival core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann kein §cWarp §7erstellen8.");
			return true;
		}
		Player player = (Player) sender;

		if (!player.hasPermission("wuffy.warp.alias.create")) {
			player.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}

		if (args.length == 2) {
			WarpHandler handler = this.core.getWarpHandler();
			String warpName = args[0];
			String aliasName = args[1];

			if (handler.exist(warpName.toLowerCase())) {
				Warp warp = handler.get(warpName.toLowerCase());

				if (!warp.aliases.stream().anyMatch(alias -> alias.alias.equalsIgnoreCase(aliasName))) {
					handler.createAlias(warp, aliasName);

					player.sendMessage(Survival.PREFIX + "§7Du hast §aerfolgreich §7für denn §aWarp §8\"§a" + warp.name + "§8\" §7denn unternamen §8\"§a" + aliasName + "§8\" §ahinzugefügt§8.");
				} else
					player.sendMessage(Survival.PREFIX + "§7Dieser Untername für denn Warp §8\"§c" + warp.name + "§8\" §7exestiert schon§8.");
				return true;
			}
			player.sendMessage(Survival.PREFIX + "§7Dieser §cWarp §7exestiert nicht§8.");
		} else {
			player.sendMessage(Survival.PREFIX + "§7/CreateAliasesWarp §8<§7warp name§8> §8<§7Untername§8>");
		}
		return true;
	}
}
