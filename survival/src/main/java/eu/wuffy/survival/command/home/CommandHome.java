package eu.wuffy.survival.command.home;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.home.Home;
import eu.wuffy.synced.util.ArrayUtil;

public class CommandHome implements CommandExecutor, TabExecutor {

	private final Survival core;

	public CommandHome(Survival core) {
		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann kein §cHome §7nutzen§8.");
			return true;
		}
		Player player = (Player) sender;

		if (!player.hasPermission("wuffy.home.use")) {
			player.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}

		if (args.length == 1) {
			Home home = this.core.getHomeHandler().get(player.getUniqueId(), args[0].toLowerCase());

			if (home == null) {
				player.sendMessage(Survival.PREFIX + "§7Dieser §cHome §7exestiert nicht§8.");
				return true;
			}

			player.teleport(home.location);
			player.sendMessage(Survival.PREFIX + "§7Du wurdest §aerfolgreich §7zum Home §8\"§a" + home.name + "§8\" §7teleportiert§8.");
		} else {
			player.sendMessage(Survival.PREFIX + "§7/Home §8<§7name§8>");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return this.core.getHomeHandler().getHomesOfPlayer(((Player) sender).getUniqueId()).stream()
					.map(home -> home.name)
					.collect(Collectors.toList());
		} else if (args.length == 1) {
			String search = args[0].toLowerCase();

			return this.core.getHomeHandler().getHomesOfPlayer(((Player) sender).getUniqueId()).stream()
					.map(home -> home.name)
					.filter(home -> home.toLowerCase().startsWith(search))
					.collect(Collectors.toList());
		}
		return ArrayUtil.EMPTY_ARRAY_LIST;
	}
}
