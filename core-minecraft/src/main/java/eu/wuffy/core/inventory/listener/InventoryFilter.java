package eu.wuffy.core.inventory.listener;

import org.bukkit.inventory.ItemStack;

public interface InventoryFilter {

	boolean isValied(ItemStack item, int slot);
}