package eu.wuffy.core.help;

import org.bukkit.inventory.ItemStack;

public interface IHelp {

	public String getName();
	public String[] getAliases();

	public String getDescription();
	public ItemStack getDisplayItem();
}
