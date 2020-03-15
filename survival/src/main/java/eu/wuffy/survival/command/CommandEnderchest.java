package eu.wuffy.survival.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;

public class CommandEnderchest implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann keine §cInventare §7öffnen§8.");
			return true;
		}
		Player player = (Player) sender;
		
		if(args.length == 0) {
			if(player.hasPermission("nizada.enderchest")) {
				player.openInventory(player.getEnderChest());
				sender.sendMessage(Survival.PREFIX + "§7Du hast deine §aEnderChest §7geöffnet§8.");
				return true;
			}
		} else if(args.length == 1) {
			if(player.hasPermission("nizada.enderchest.other")) {
				Player target = Bukkit.getPlayer(args[0]);
				
				if(target != null) {
					player.openInventory(target.getEnderChest());
					
					if(player.equals(target))
						sender.sendMessage(Survival.PREFIX + "§7Du hast deine §aEnderChest §7geöffnet§8.");
					else
						player.sendMessage(Survival.PREFIX + "§7Du siehst nun die EnderChest von §8\"§a" + target.getName() + "§8\"§8.");
				} else
					sender.sendMessage(Survival.PREFIX + "§7Der Angegebene Spieler §8\"§c" + args[1] + "§8\" §7ist nicht §cOnline§8.");
				return true;
			}
		}
		sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
		return true;
	}
}