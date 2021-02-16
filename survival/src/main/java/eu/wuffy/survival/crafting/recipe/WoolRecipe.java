package eu.wuffy.survival.crafting.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.survival.crafting.IRecipe;

public class WoolRecipe extends ShapelessRecipe implements IRecipe {

	public static final ItemStack STRING_ITEMSTACK = new ItemFactory(Material.WHITE_WOOL).build();

	public WoolRecipe() {
		super(NamespacedKey.minecraft("recipe.wool"), STRING_ITEMSTACK);

		this.addIngredient(4, Material.STRING);
		this.setGroup("wool");
	}

	@Override
	public NamespacedKey getNamespaceKey() {
		return this.getKey();
	}
}