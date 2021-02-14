package eu.wuffy.survival.command.admin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.survival.Survival;
import eu.wuffy.survival.enchantment.EnchantmentList;
import eu.wuffy.synced.IHandler;
import eu.wuffy.synced.config.ConfigService;

public class CommandSurvival implements CommandExecutor {

//	private Survival core;

	public CommandSurvival(Survival core) {
//		this.core = core;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("wuffy.admin")) {
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
			return true;
		}

		if (args.length > 0) {
			switch (args[0]) {
			case "test":
				ItemStack item = new ItemFactory(Material.DIAMOND_AXE).setDisplayName("§cTesting").build();
				EnchantmentList.TREE_FELLER.applyOn(item, 1);
				((Player) sender).getInventory().addItem(item);
				return true;
			case "test2":
				EnchantmentList.TREE_FELLER.applyOn(((Player) sender).getInventory().getItemInMainHand(), 3);
				return true;
			case "test3":
				sender.sendMessage("Has enchantment: " + EnchantmentList.TREE_FELLER.getEnchantLevel(((Player) sender).getInventory().getItemInMainHand()));
				return true;

			case "rl":
			case "reload":
				IHandler.getHandlers().forEach(IHandler::disable);
				ConfigService.removeAllConfigs();
				IHandler.getHandlers().forEach(IHandler::enable);
				sender.sendMessage(Survival.PREFIX + "§7Survival §aHandler §7und §aConfigs §7wurden neugeladen§8.");
				return true;

			default:
				sender.sendMessage(Survival.PREFIX + "§7Der angegebene command konnte nicht gefunden werden§8.");
				return true;
			}
		}
		sender.sendMessage(Survival.PREFIX + "§7/Survival reload §8<§7dynmap§8>§8.");
		return true;
	}
}