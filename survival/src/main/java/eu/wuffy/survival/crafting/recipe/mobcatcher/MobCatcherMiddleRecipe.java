package eu.wuffy.survival.crafting.recipe.mobcatcher;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.survival.crafting.IRecipe;

public class MobCatcherMiddleRecipe extends ShapedRecipe implements IRecipe {

	public static final ItemStack ITEMSTACK = new ItemFactory(Material.SNOWBALL)
			.setDisplayName("MobCatcher")
			.setDamage(15)
			.buildCustomModel(2);

	public MobCatcherMiddleRecipe() {
		super(NamespacedKey.minecraft("recipe.mobcatcher.middle"), ITEMSTACK);

		this.shape("CIC", "DND", "CIC");
		this.setIngredient('C', new RecipeChoice.ExactChoice(MobCatcherShellRecipe.ITEMSTACK));
		this.setIngredient('D', Material.DIAMOND);
		this.setIngredient('N', Material.NETHERITE_SCRAP);
		this.setIngredient('I', Material.NETHERITE_INGOT);
		this.setGroup("mobcatcher");
	}

	@Override
	public boolean isDefault(Player player) {
		return false;
	}

	@Override
	public NamespacedKey getNamespaceKey() {
		return this.getKey();
	}
}