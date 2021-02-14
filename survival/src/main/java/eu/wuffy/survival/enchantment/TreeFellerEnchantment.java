package eu.wuffy.survival.enchantment;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class TreeFellerEnchantment extends CustomEnchantment {

	public TreeFellerEnchantment() {
		super(NamespacedKey.minecraft("enchantment.treefeller"), "TreeFeller");
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return item.getType() == Material.DIAMOND_AXE;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.TOOL;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}
}