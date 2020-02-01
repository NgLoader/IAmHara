package eu.wuffy.survival.command.admin;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.survival.Survival;

public class CommandAdminTool implements CommandExecutor {

	private static final ItemStack WORLDEDIT_WAND = new ItemFactory(Material.WOODEN_AXE).addAllFlag()
			.setDisplayName("§5Magische Axt").addLore("§7Kann zaubern.").build();
	private static final ItemStack WORLDEDIT_COMPASS = new ItemFactory(Material.COMPASS).addAllFlag()
			.setDisplayName("§5Compass").addLore("§7Ist fast wie ein Enderman.").build();
	private static final ItemStack GRIVERPROVENTION_CLAIMINFO = new ItemFactory(Material.STICK).addAllFlag()
			.setDisplayName("§aClaim Info").addLore("§7Kann dir grundstücke anzeigen.").build();
	private static final ItemStack GRIVERPROVENTION_CLAIMTOOL = new ItemFactory(Material.GOLDEN_SHOVEL).addAllFlag()
			.setDisplayName("§aClaim Schaufel").addLore("§7Kann dir ein grundstück erstellen.").build();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Survival.PREFIX + "§7Die Console kann sich keine §cAdmin §4Tool §7geben§8.");
			return true;
		}
		Player player = (Player) sender;

		if (player.hasPermission("wuffy.admintool")) {
			CommandAdminTool.setAdminTool(player.getInventory());
			sender.sendMessage(Survival.PREFIX + "§7Du hast nun dass §aAdmin Tool§8.");
		} else
			sender.sendMessage(Survival.PREFIX + "§7Du hast keine §cRechte §7um diesen §cCommand §7zu nutzen§8.");
		return true;
	}

	public static final void setAdminTool(Inventory inventory) {
		inventory.setItem(0, WORLDEDIT_WAND);
		inventory.setItem(4, WORLDEDIT_COMPASS);
		inventory.setItem(7, GRIVERPROVENTION_CLAIMINFO);
		inventory.setItem(8, GRIVERPROVENTION_CLAIMTOOL);
	}
}