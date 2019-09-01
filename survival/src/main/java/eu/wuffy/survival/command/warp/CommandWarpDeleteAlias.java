package eu.wuffy.survival.command.warp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.warp.Warp;
import eu.wuffy.survival.warp.WarpHandler;

public class CommandWarpDeleteAlias implements CommandExecutor {

	private Survival core;

	public CommandWarpDeleteAlias(Survival core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann kein §cWarp §7löschen§8.");
			return true;
		}
		Player player = (Player) sender;

		if (!player.hasPermission("wuffy.warp.alias.delete")) {
			player.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}

		if (args.length == 1) {
			String alisesName = args[0];
			WarpHandler handler = this.core.getWarpHandler();

			if (handler.exist(alisesName.toLowerCase())) {
				Warp warp = handler.get(alisesName.toLowerCase());

				if (warp.aliases.stream().anyMatch(alias -> alias.alias.equalsIgnoreCase(alisesName))) {
					handler.deleteAlias(warp.aliases.stream().filter(alias -> alias.alias.equalsIgnoreCase(alisesName)).findFirst().get());

					player.sendMessage(Survival.PREFIX + "§7Du hast §aerfolgreich §7für denn §aWarp §8\"§a" + warp.name + "§8\" §7denn unternamen §8\"§a" + alisesName + "§8\" §centfernt§8.");
				} else
					player.sendMessage(Survival.PREFIX + "§7Dieser Untername f§r denn Warp §8\"§c" + warp.name + "§8\" §7exestiert nicht§8.");
				return true;
			}
			player.sendMessage(Survival.PREFIX + "§7Dieser §cWarp §7exestiert nicht§8.");
		} else {
			player.sendMessage(Survival.PREFIX + "§7/RemoveAliasesWarp §8<§7Untername§8>");
		}
		return true;
	}
}
