package eu.wuffy.survival.command.home;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.wuffy.core.util.GroupUtil;
import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.home.Home;
import eu.wuffy.survival.handler.home.HomeHandler;
import me.lucko.luckperms.api.LuckPermsApi;

public class CommandHomeCreate implements CommandExecutor {

	private final Survival core;

	public CommandHomeCreate(Survival core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann kein §cHome §7erstellen§8.");
			return true;
		}
		Player player = (Player) sender;

		if (!player.hasPermission("wuffy.home.create")) {
			player.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}

		if (args.length >= 1) {
			HomeHandler handler = this.core.getHomeHandler();
			String homeName = args[0];
			LuckPermsApi luckPermsApi = this.core.getLuckPermsHandler().getApi().get();
			int homes = Integer.valueOf(GroupUtil.getGroupMetaSorted(luckPermsApi.getGroup(luckPermsApi.getUserSafe(player.getUniqueId()).get().getPrimaryGroup()), "max-homes", "10"));

			if (!player.hasPermission("wuffy.home.nohomelimit") && handler.getHomesOfPlayer(player.getUniqueId()).size() >= homes) {
				player.sendMessage(Survival.PREFIX + "§7Du darfst nur maximal §c" + homes + " §7Homes §cbesitzen§8.");
				return true;
			}

			List<String> description = new ArrayList<>();
			for (int i = 1; i < args.length; i++)
				description.add(args[i]);

			if (handler.exist(player.getUniqueId(), homeName.toLowerCase())) {
				player.sendMessage(Survival.PREFIX + "§7Dieser §cHome §7exestiert schon§8.");
				return true;
			}

			try {
				Home home = handler.create(player.getUniqueId(), homeName, String.join(" ", description), player.getLocation());
				player.sendMessage(Survival.PREFIX + "§7Du hast erfolgreich dein §aHome §8\"§a" + home.name + "§8\" §aerstellt§8.");
			} catch (SQLException e) {
				e.printStackTrace();
				player.sendMessage(Survival.PREFIX + "§4cEin fehler ist aufgetreten§8.");
			}
		} else {
			player.sendMessage(Survival.PREFIX + "§7/SetHome §8<§7name§8> §8[§7beschreibung§8]");
		}
		return true;
	}
}
