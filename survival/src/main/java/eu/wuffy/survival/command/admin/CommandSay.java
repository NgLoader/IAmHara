package eu.wuffy.survival.command.admin;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;

public class CommandSay implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("nizada.say")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}
		String senderColor = sender instanceof Player ? "§c" : "§4";
		String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", Arrays.asList(args)));
		while (message.startsWith(" "))
			message = message.substring(1);

		for (Player online : Bukkit.getOnlinePlayers())
			if (online.hasPermission("nizada.say.see"))
				online.sendMessage("§c" + senderColor + sender.getName() + " §8» §c" + message);
			else
				online.sendMessage("§4Server §8» §c" + message);
		Bukkit.getConsoleSender().sendMessage("§c" + senderColor + sender.getName() + " §8» §c" + message);
		return true;
	}
}