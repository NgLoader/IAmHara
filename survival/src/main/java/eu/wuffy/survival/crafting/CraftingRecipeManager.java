package eu.wuffy.survival.crafting;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.crafting.recipe.InvisibleItemFrameRecipe;
import eu.wuffy.survival.crafting.recipe.WebRecipe;
import eu.wuffy.survival.crafting.recipe.WoolRecolorRecipe;
import eu.wuffy.survival.crafting.recipe.WoolStringRecipe;
import eu.wuffy.synced.IHandler;

public class CraftingRecipeManager extends IHandler<Survival> implements Listener {

	private final Set<IRecipe> recipes = new LinkedHashSet<>();

	public CraftingRecipeManager(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.recipes.add(new InvisibleItemFrameRecipe(this.core));

		this.recipes.add(new WebRecipe());
		this.recipes.add(new WoolStringRecipe());
		for (Material material : Material.values()) {
			String name = material.name();
			if (name.endsWith("_DYE")) {
				Material wool = Material.valueOf(name.replace("_DYE", "") + "_WOOL");
				Bukkit.getRecipesFor(new ItemStack(wool)).forEach(recipe -> {
					if (recipe instanceof ShapedRecipe) {
						Bukkit.removeRecipe(((ShapedRecipe) recipe).getKey());
					} else if (recipe instanceof ShapelessRecipe) {
						Bukkit.removeRecipe(((ShapelessRecipe) recipe).getKey());
					}
				});
				this.recipes.add(new WoolRecolorRecipe(material, wool));
			}
		}
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this.core);

		for (IRecipe recipe : this.recipes) {
			try {
				Bukkit.addRecipe(recipe);

				if (recipe instanceof Listener) {
					Bukkit.getPluginManager().registerEvents((Listener) recipe, this.core);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDisable() {
		this.recipes.forEach(recipe -> Bukkit.removeRecipe(recipe.getNamespaceKey()));
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		for (IRecipe recipe : this.recipes) {
			if (recipe.isDefault(player)) {
				player.discoverRecipe(recipe.getNamespaceKey());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onCraftItem(CraftItemEvent event) {
		IRecipe recipe = this.getCustomRecipe(event.getRecipe());
		if (recipe != null) {
			recipe.onCraft(event.getWhoClicked(), event);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		IRecipe recipe = this.getCustomRecipe(event.getRecipe());
		if (recipe != null) {
			recipe.onPrepareCraft(event.getViewers(), event);
		}
	}

	public IRecipe getCustomRecipe(Recipe recipe) {
		if (recipe != null) {
			ItemStack result = recipe.getResult();
			for (IRecipe customRecipe : this.recipes) {
				if (customRecipe.getResult().equals(result)) {
					return customRecipe;
				}
			}
		}
		return null;
	}
}
