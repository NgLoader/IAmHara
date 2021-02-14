package eu.wuffy.survival.crafting;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;

public interface IRecipe extends Recipe {

	public NamespacedKey getNamespaceKey();

	public default void onCraft(HumanEntity entity, CraftItemEvent event) { }
	public default void onPrepareCraft(List<HumanEntity> entitys, PrepareItemCraftEvent event) { }

	public default boolean isDefault(Player player) {
		return true;
	}
}
