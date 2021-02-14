package eu.wuffy.survival.crafting.recipe.mobcatcher;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.survival.crafting.IRecipe;

public class MobCatcherShellRecipe extends ShapedRecipe implements IRecipe {

	public static final ItemStack WEB_ITEMSTACK = new ItemFactory(Material.COBWEB).build();

	public MobCatcherShellRecipe() {
		super(NamespacedKey.minecraft("recipe.web"), WEB_ITEMSTACK);

		this.shape("SSS", "SSS", "SSS");
		this.setIngredient('S', Material.STRING);
	}

	@Override
	public NamespacedKey getNamespaceKey() {
		return this.getKey();
	}
}