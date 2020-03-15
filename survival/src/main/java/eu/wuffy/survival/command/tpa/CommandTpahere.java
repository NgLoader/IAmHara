package eu.wuffy.survival.command.tpa;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.tpa.TPAHandler;
import eu.wuffy.survival.handler.tpa.TPARequests;

public class CommandTpahere implements CommandExecutor, TabExecutor {

	private final TPAHandler tpaHandler;

	public CommandTpahere(Survival survival) {
		this.tpaHandler = survival.getTpaHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann keine §cteleport §7command nutzen§8.");
			return true;
		}
		if (!sender.hasPermission("survival.tpa")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7diesen command zu nutzen§8.");
			return true;
		}

		Player player = (Player) sender;
		Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : null;

		if (target == null) {
			sender.sendMessage(Survival.PREFIX + "§7Der angegebene §aSpieler §7ist nicht online§8.");
			return true;
		} else if (player.equals(target)) {
			player.sendMessage(Survival.PREFIX + "Du kannst keine §canfragen §7an dir selber stellen§8.");
			return true;
		}

		TPARequests requests = this.tpaHandler.getRequests(target);

		if (requests.getCount() > 9) {
			player.sendMessage(Survival.PREFIX + "§7Der Spieler §8\"§6" + target.getName() + "§8\" §7hat derzeitig zuviele anfragen§8.");
			return true;
		}

		if (!requests.canRequest(player)) {
			player.sendMessage(Survival.PREFIX + "§7Du hast eine §aTeleportanfrage §7an §8\"§6" + player.getName() + "§8 §7bereits §cgesendet§8.\"");
			return true;
		}

		if (requests.addRequest(player, player.getLocation())) {
			return true;
		}

		player.sendMessage(Survival.PREFIX + "Es ist ein §cFehler §7aufgetreten§8.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			String search = args[0].toLowerCase();

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