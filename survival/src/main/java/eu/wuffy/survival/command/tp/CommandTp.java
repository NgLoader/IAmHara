package eu.wuffy.survival.command.tp;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import eu.wuffy.core.util.RegexUtil;
import eu.wuffy.core.util.SearchUtil;
import eu.wuffy.survival.Survival;

public class CommandTp implements CommandExecutor, TabExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Survival.PREFIX + "§7Die Console kann diese §ccommand §7nicht nutzen§8.");
				return true;
			}
			if (!sender.hasPermission("survival.tp")) {
				sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7diesen command zu nutzen§8.");
				return true;
			}

			Player player = (Player) sender;
			Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : null;

			if (target == null) {
				player.sendMessage(Survival.PREFIX + "§7Der angegebene spieler §8\"§6" + args[0] + "§8\" §7wurde nicht gefunden§8.");
				return true;
			}

			player.teleport(target);
			player.sendMessage(Survival.PREFIX + "§7Du hast dich §aerfolgreich §7zu §8\"§6" + target.getDisplayName() + "§8\" §7teleportiert§8.");

			if (target.hasPermission("survival.tp.show")) {
				target.sendMessage(Survival.PREFIX + "§7Der Spieler §8\"§6" + player.getDisplayName() + "§8\" §7hat sich zu dir §ateleportiert§8.");
			}
			return true;
		} else if (args.length == 2) {
			if (!sender.hasPermission("survival.tp.other")) {
				sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7diesen command zu nutzen§8.");
				return true;
			}

			List<Player> players = SearchUtil.searchPlayers(args[0], true);
			Player target = Bukkit.getPlayer(args[1]);

			if (players.isEmpty()) {
				sender.sendMessage(Survival.PREFIX + "§7Der angegebene spieler §8\"§6" + args[0] + "§8\" §7wurde nicht gefunden§8.");
				return true;
			}
			if (target == null) {
				sender.sendMessage(Survival.PREFIX + "§7Der angegebene target spieler §8\"§6" + args[0] + "§8\" §7wurde nicht gefunden§8.");
				return true;
			}

			for (Player player : players) {
				player.teleport(target);

				if (player.hasPermission("survival.tp.show")) {
					player.sendMessage(Survival.PREFIX + "§7Der Spieler §8\"§6" + sender.getName() + "§8\" §7hat dich zu §8\"§6" + target.getDisplayName() + "§8\" §ateleportiert§8.");
				}
			}

			if (players.size() > 1) {
				target.sendMessage(Survival.PREFIX + "§7Du hast §8\"§6" + players.size() + "§8\" §7Spieler zu §8\"§6" + target.getDisplayName() + "§8\" §ateleportiert§8.");
				return true;
			}

			sender.sendMessage(Survival.PREFIX + "§7Du hast §8\"§6" + players.get(0).getDisplayName() + "§8\" §7zu §8\"§6" + target.getDisplayName() + "§8\" §ateleportiert§8.");
			return true;
		} else if (args.length == 3) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Survival.PREFIX + "§7Die Console kann diese §ccommand §7nicht nutzen§8.");
				return true;
			}
			if (!sender.hasPermission("survival.tp.location")) {
				sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7diesen command zu nutzen§8.");
				return true;
			}

			Player player = (Player) sender;
			Location currentLocation = player.getLocation();
			double x, y, z;

			if (args[0].startsWith("~")) {
				x = currentLocation.getX();

				String multiply = args[0].substring(1);
				if (!RegexUtil.isNumber(multiply)) {
					sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
					return true;
				}

				x += Double.valueOf(multiply);
			} else {
				if (!RegexUtil.isNumber(args[0])) {
					sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
					return true;
				}

				x = Double.valueOf(args[0]);
			}

			if (args[1].startsWith("~")) {
				y = currentLocation.getY();

				String multiply = args[1].substring(1);
				if (!RegexUtil.isNumber(multiply)) {
					sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
					return true;
				}

				y += Double.valueOf(multiply);
			} else {
				if (!RegexUtil.isNumber(args[1])) {
					sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
					return true;
				}

				y = Double.valueOf(args[1]);
			}

			if (args[2].startsWith("~")) {
				z = currentLocation.getZ();

				String multiply = args[2].substring(1);
				if (!RegexUtil.isNumber(multiply)) {
					sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
					return true;
				}

				z += Double.valueOf(multiply);
			} else {
				if (!RegexUtil.isNumber(args[2])) {
					sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
					return true;
				}

				z = Double.valueOf(args[2]);
			}

			player.teleport(new Location(player.getWorld(), x, y, z, currentLocation.getYaw(), currentLocation.getPitch()));
			sender.sendMessage(Survival.PREFIX + "§7Du wurdest zu deiner §alocation §7gesendet§8.");
			return true;
		} else if (args.length == 4) {
			if (!sender.hasPermission("survival.tp.location.other")) {
				sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7diesen command zu nutzen§8.");
				return true;
			}

			boolean isPlayer = (sender instanceof Player);
			double x, y, z;

			if (args[0].startsWith("~")) {
				if (!isPlayer) {
					sender.sendMessage(Survival.PREFIX + "§7Die Console kann hat keine §clocation§8.");
				}

				x = ((Player) sender).getLocation().getX();

				String multiply = args[0].substring(1);
				if (!RegexUtil.isNumber(multiply)) {
					sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
					return true;
				}

				x += Double.valueOf(multiply);
			} else {
				if (!RegexUtil.isNumber(args[0])) {
					sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
					return true;
				}

				x = Double.valueOf(args[0]);
			}

			if (args[1].startsWith("~")) {
				if (!isPlayer) {
					sender.sendMessage(Survival.PREFIX + "§7Die Console kann hat keine §clocation§8.");
				}

				y = ((Player) sender).getLocation().getY();

				String multiply = args[1].substring(1);
				if (!RegexUtil.isNumber(multiply)) {
					sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
					return true;
				}

				y += Double.valueOf(multiply);
			} else {
				if (!RegexUtil.isNumber(args[1])) {
					sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
					return true;
				}

				y = Double.valueOf(args[1]);
			}

			if (args[2].startsWith("~")) {
				if (!isPlayer) {
					sender.sendMessage(Survival.PREFIX + "§7Die Console kann hat keine §clocation§8.");
				}

				z = ((Player) sender).getLocation().getZ();

				String multiply = args[2].substring(1);
				if (!RegexUtil.isNumber(multiply)) {
					sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
					return true;
				}

				z += Double.valueOf(multiply);
			} else {
				if (!RegexUtil.isNumber(args[2])) {
					sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
					return true;
				}

				z = Double.valueOf(args[2]);
			}

			Location targetLocation = null;
			List<Player> players = SearchUtil.searchPlayers(args[3], true);

			if (sender instanceof Player) {
				Player player = (Player) sender;
				targetLocation = new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
			} else if (players.size() == 1) {
				Player player = players.get(0);
				targetLocation = new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
			} else {
				targetLocation = new Location(Bukkit.getWorld("world"), x, y, z);
			}

			if (players.isEmpty()) {
				sender.sendMessage(Survival.PREFIX + "§7Der angegebene spieler §8\"§6" + args[3] + "§8\" §7wurde nicht gefunden§8.");
				return true;
			}

			for (Player player : players) {
				player.teleport(targetLocation);

				if (player.hasPermission("survival.tp.show") && player != sender) {
					player.sendMessage(Survival.PREFIX + "§7Du wurdest von §8\"§6" + sender.getName() + "§8\" §7teleportiert§8.");
				}
			}

			if (players.size() > 1) {
				sender.sendMessage(Survival.PREFIX + "§7Du hast §8\"§6" + players.size() + "§8\" §7spieler §ateleportiert§8.");
				return true;
			}

			sender.sendMessage(Survival.PREFIX + "§7Du hast §8\"§6" + players.get(0).getDisplayName() + "§8\" §7spieler §ateleportiert§8.");
			return true;
		}

		sender.sendMessage(Survival.PREFIX + "§8/§7tp §8<§7Player§8> §8[§7Target§8]\n"
						 + Survival.PREFIX + "§8/§7tp §8<§7X§8> §8<§7Y§8> §8<§7Z§8> §8[§7Target§8]");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length > 0) {
			String search = args[args.length - 1].toLowerCase();

			return Bukkit.getOnlinePlayers().stream()
					.map(request -> request.getDisplayName())
					.filter(request -> request.toLowerCase().startsWith(search))
					.collect(Collectors.toList());
		}

		return Bukkit.getOnlinePlayers().stream()
				.map(request -> request.getDisplayName())
				.collect(Collectors.toList());
	}
}