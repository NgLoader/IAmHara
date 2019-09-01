package eu.wuffy.survival.command.home;

import java.sql.SQLException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.home.Home;
import eu.wuffy.survival.home.HomeHandler;

public class CommandHomeDelete implements CommandExecutor {

	private final Survival core;

	public CommandHomeDelete(Survival core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann kein §cHome §7löschen§8.");
			return true;
		}
		Player player = (Player) sender;

		if (!player.hasPermission("wuffy.home.delete")) {
			player.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}

		if (args.length == 1) {
			HomeHandler handler = this.core.getHomeHandler();
			String homeName = args[0].toLowerCase();
			Home home = handler.get(player.getUniqueId(), homeName);

			if (home == null) {
				player.sendMessage(Survival.PREFIX + "§7Dieser §cHome §7exestiert nicht§8.");
				return true;
			}

			try {
				handler.delete(player.getUniqueId(), home);
				player.sendMessage(Survival.PREFIX + "§7Du hast erfolgreich dein §aHome §8\"§a" + home.name + "§8\" §cgelöscht§8.");
			} catch (SQLException e) {
				e.printStackTrace();
				player.sendMessage(Survival.PREFIX + "§4cEin fehler ist aufgetreten§8.");
			}
		} else {
			player.sendMessage(Survival.PREFIX + "§7/DeleteHome §8<§7name§8>");
		}
		return true;
	}
}
