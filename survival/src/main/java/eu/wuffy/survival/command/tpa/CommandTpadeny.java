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

public class CommandTpadeny implements CommandExecutor, TabExecutor {

	private final TPAHandler tpaHandler;

	public CommandTpadeny(Survival survival) {
		this.tpaHandler = survival.getTpaHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann keine §cteleport §7command nutzen§8.");
			return true;
		}
		if (!sender.hasPermission("survival.tpa.deny")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7diesen command zu nutzen§8.");
			return true;
		}

		Player player = (Player) sender;
		TPARequests requests = this.tpaHandler.getRequests(player);
		Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : null;

		if (target == null && args.length == 0) {
			target = requests.getLastRequest();
		}

		if (target == null) {
			sender.sendMessage(Survival.PREFIX + "§7Der angegebene §aSpieler §7ist nicht online, oder du hast keine anfragen§8.");
			return true;
		}

		if (!requests.canRequest(target)) {
			if (!requests.denyRequest(target)) {
				player.sendMessage(Survival.PREFIX + "§7Es ist ein §cFehler §7aufgetreten§8.");
			}
			return true;
		}

		player.sendMessage(Survival.PREFIX + "§7Du hast keine §aTeleportanfrage §7von §8\"§6" + target.getName() + "§8\" §cbekommen§8.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length > 1) {
			return null;
		}

		Player player = (Player) sender;
		TPARequests requests = this.tpaHandler.getRequests(player);

		if (args.length == 1) {
			String search = args[0].toLowerCase();

			return requests.getRequests().stream()
					.map(request -> request.getDisplayName())
					.filter(request -> request.toLowerCase().startsWith(search))
					.collect(Collectors.toList());
		}

		return requests.getRequests().stream()
				.map(request -> request.getDisplayName())
				.collect(Collectors.toList());
	}
}