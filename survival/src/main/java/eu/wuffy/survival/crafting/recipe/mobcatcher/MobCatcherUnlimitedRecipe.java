package eu.wuffy.survival.crafting.recipe.mobcatcher;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.survival.crafting.IRecipe;

public class MobCatcherUnlimitedRecipe extends ShapedRecipe implements IRecipe {

	public static final ItemStack ITEMSTACK = new ItemFactory(Material.SNOWBALL)
			.setDisplayName("MobCatcher")
			.setDamage(-1)
			.buildCustomModel(2);

	public MobCatcherUnlimitedRecipe() {
		super(NamespacedKey.minecraft("recipe.mobcatcher.unlimited"), ITEMSTACK);

		this.shape("CNC", "DSD", "CNC");
		this.setIngredient('C', new RecipeChoice.ExactChoice(MobCatcherShellRecipe.ITEMSTACK));
		this.setIngredient('D', Material.DIAMOND_BLOCK);
		this.setIngredient('N', Material.NETHERITE_BLOCK);
		this.setIngredient('S', Material.NETHER_STAR);
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