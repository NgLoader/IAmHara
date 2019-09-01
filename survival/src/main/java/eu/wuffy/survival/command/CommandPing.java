package eu.wuffy.survival.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.wuffy.core.util.NMSUtil;
import eu.wuffy.survival.Survival;

public class CommandPing implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console hat keinen §4Ping§8.");
			return true;
		}
		sender.sendMessage(Survival.PREFIX + "§7Dein §aPing §7beträgt §a" + NMSUtil.getPlayerPing((Player) sender) + "§cms§8.");
		return true;
	}
}
