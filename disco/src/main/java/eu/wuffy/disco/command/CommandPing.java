package eu.wuffy.disco.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.wuffy.core.util.PlayerUtil;
import eu.wuffy.disco.Disco;

public class CommandPing implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Disco.PREFIX + "§7Die Console hat keinen §4Ping§8.");
			return true;
		}
		sender.sendMessage(Disco.PREFIX + "§7Dein §aPing §7beträgt §a" + PlayerUtil.getPlayerPing((Player) sender) + "§cms§8.");
		return true;
	}
}
