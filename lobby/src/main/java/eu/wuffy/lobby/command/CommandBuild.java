package eu.wuffy.lobby.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.wuffy.lobby.Lobby;
import eu.wuffy.lobby.handler.BuildHandler;

public class CommandBuild implements CommandExecutor {

	private final Lobby core;
	private final BuildHandler buildHandler;

	public CommandBuild(Lobby core) {
		this.core = core;

		this.buildHandler = this.core.getBuildHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("wuffy.buildmode")) {
			sender.sendMessage(Lobby.PREFIX + "§7Du hast keine §cberechtigung §7um diesen §ccommand §7zu nutzen§8.");
			return true;
		}

		if (args.length == 0) {
			if (sender instanceof Player) {
				if (this.buildHandler.isInBuildMode((Player) sender)) {
					this.buildHandler.removeFromBuildMode((Player) sender);
					sender.sendMessage(Lobby.PREFIX + "§7Der §aBuild Modus §7wurde §cdeaktiviert§8.");
					return true;
				} else {
					this.buildHandler.addToBuildMode((Player) sender);
					sender.sendMessage(Lobby.PREFIX + "§7Der §aBuild Modus §7wurde §aaktiviert§8.");
					return true;
				}
			} else {
				sender.sendMessage(Lobby.PREFIX + "§7Du musst ein §aSpieler §7sein§8.");
				return true;
			}
		} else {
			Player target = Bukkit.getPlayer(args[0]);

			if (target != null) {
				if (target == sender) {
					sender.sendMessage(Lobby.PREFIX + "§7Du kannst dich nicht selber auswählen§8!");
					return true;
				}

				if (this.buildHandler.isInBuildMode(target)) {
					this.buildHandler.removeFromBuildMode(target);
					sender.sendMessage(Lobby.PREFIX + "§7Der §aBuild Modus von §8'§a" + target.getName() + "§8' §7wurde §cdeaktiviert§8.");
					target.sendMessage(Lobby.PREFIX + "§7Der §aBuild Modus §7wurde §cdeaktiviert§8.");
					return true;
				} else {
					this.buildHandler.addToBuildMode(target);
					sender.sendMessage(Lobby.PREFIX + "§7Der §aBuild Modus von §8'§a" + target.getName() + "§8' §7wurde §aaktiviert§8.");
					target.sendMessage(Lobby.PREFIX + "§7Der §aBuild Modus §7wurde §aaktiviert§8.");
					return true;
				}
			}

			sender.sendMessage(Lobby.PREFIX + "§7Der Spieler §8'§c" + args[0] + "§8' §7wurde nicht gefunden§8.");
			return true;
		}
	}
}