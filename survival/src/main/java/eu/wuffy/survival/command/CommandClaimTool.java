package eu.wuffy.survival.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.survival.Survival;

public class CommandClaimTool implements CommandExecutor {
	
	private static final HashMap<UUID, Long> COULDOWN = new HashMap<>();
	private static final ItemStack CLAIM_TOOL = new ItemFactory(Material.GOLDEN_SHOVEL).setDisplayName("§aClaim §2Schaufel").addLore("§7Erstelle oder erweitere dein Zuhause.").build();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann sich keine §cItems §4geben§8.");
			return true;
		}
		Player player = (Player) sender;
		
		if(COULDOWN.containsKey(player.getUniqueId())) {
			if(COULDOWN.get(player.getUniqueId()) > System.currentTimeMillis()) {
				player.sendMessage(Survival.PREFIX + "§7Bitte warte noch §8\"§c" + ((int) ((COULDOWN.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000)) + "§8\" §7Sekunden§8.");
				return true;
			} else
				COULDOWN.remove(player.getUniqueId());
		}
		
		if (player.getInventory().getContents() != null && Arrays.asList(player.getInventory().getContents()).stream().anyMatch(item -> isClaimTool(item))) {
			player.sendMessage(Survival.PREFIX + "§7Du hast bereits eine §aClaim Schaufel §7im §2Inventar§8.");
			return true;
		}
		if (player.getEnderChest() != null && player.getEnderChest().getContents() != null && Arrays.asList(player.getEnderChest().getContents()).stream().anyMatch(item -> isClaimTool(item))) {
			player.sendMessage(Survival.PREFIX + "§7Du hast bereits eine §aClaim Schaufel §7in deiner §2Enderchest§8.");
			return true;
		}
		
		COULDOWN.put(player.getUniqueId(), System.currentTimeMillis() + 900000); // 1000 * 60 * 15
		player.getInventory().addItem(CLAIM_TOOL);
		player.sendMessage(Survival.PREFIX + "§7Dir wurde eine §aClaim Schaufel §7gegeben§8.");
		return true;
	}
	
	public static boolean isClaimTool(ItemStack item) {
		return item != null && item.getType() == CLAIM_TOOL.getType() && item.hasItemMeta() && item.getItemMeta().hasLore();
	}
}