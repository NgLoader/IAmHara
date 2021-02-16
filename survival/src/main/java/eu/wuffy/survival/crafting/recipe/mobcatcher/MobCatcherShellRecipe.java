package eu.wuffy.survival.crafting.recipe.mobcatcher;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.survival.crafting.IRecipe;

public class MobCatcherShellRecipe extends ShapedRecipe implements IRecipe {

	public static final ItemStack ITEMSTACK = new ItemFactory(Material.SNOWBALL).setDisplayName("MobCatcher Shell").buildCustomModel(1);

	public MobCatcherShellRecipe() {
		super(NamespacedKey.minecraft("recipe.mobcatcher.shell"), ITEMSTACK);

		this.shape("LWL", "BCB", "LWL");
		this.setIngredient('L', Material.LEAD);
		this.setIngredient('W', Material.COBWEB);
		this.setIngredient('B', Material.IRON_BARS);
		this.setIngredient('C', Material.CHEST);
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