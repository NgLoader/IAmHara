package eu.wuffy.survival.command.tp;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import eu.wuffy.core.util.SearchUtil;
import eu.wuffy.survival.Survival;

public class CommandTpHere implements CommandExecutor, TabExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann keine §cWarp §7nutzen§8.");
			return true;
		}
		if (!sender.hasPermission("survival.tp.here")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7diesen command zu nutzen§8.");
			return true;
		}

		if (args.length == 1) {
			List<Player> players = SearchUtil.searchPlayers(args[0], true);
			players.remove(sender);

			if (players.isEmpty()) {
				sender.sendMessage(Survival.PREFIX + "§7Der angegebene spieler §8\"§6" + args[0] + "§8\" §7wurde nicht gefunden§8.");
				return true;
			}

			for (Player player : players) {
				player.teleport((Player) sender);

				if (player.hasPermission("survival.tp.show")) {
					player.sendMessage(Survival.PREFIX + "§7Der Spieler §8\"§6" + sender.getName() + "§8\" §7hat dich zu §8\"§6" + sender.getName() + "§8\" §ateleportiert§8.");
				}
			}

			if (players.size() > 1) {
				sender.sendMessage(Survival.PREFIX + "§7Du hast §8\"§6" + players.size() + "§8\" §7Spieler zu §8\"§6" + sender.getName() + "§8\" §ateleportiert§8.");
				return true;
			}

			sender.sendMessage(Survival.PREFIX + "§7Du hast §8\"§6" + players.get(0).getDisplayName() + "§8\" §7zu §8\"§6" + sender.getName() + "§8\" §ateleportiert§8.");
		}

		sender.sendMessage(Survival.PREFIX + "§8/§7tphere §8<§7Player§8>");
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