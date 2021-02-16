package eu.wuffy.survival.crafting.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.survival.crafting.IRecipe;

public class WoolToStringRecipe extends ShapelessRecipe implements IRecipe {

	public static final ItemStack STRING_ITEMSTACK = new ItemFactory(Material.STRING).setNumber(4).build();

	public WoolToStringRecipe() {
		super(NamespacedKey.minecraft("recipe.woolstring"), STRING_ITEMSTACK);

		this.addIngredient(Material.SHEARS);
		this.addIngredient(new RecipeChoice.MaterialChoice(Tag.WOOL));
	}

	@Override
	public void onCraft(HumanEntity entity, CraftItemEvent event) {
		Inventory inventory = event.getClickedInventory();
		ItemStack[] contents = inventory.getContents();

		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
			return;
		}

		ItemStack shears = null, wool = null;
		for (int i = 0; i < contents.length; i++) {
			ItemStack item = contents[i];
			if (item.getType() == Material.SHEARS) {
				if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
					shears = item;
					continue;
				}
//
//				event.setCurrentItem(null);
//				event.setCancelled(true);
//				event.setResult(Result.DENY);

				int damage = item.hasItemMeta() ? ((Damageable) item.getItemMeta()).getDamage() + 2 : 2;
				if (item.getType().getMaxDurability() > damage + 1) {
					for (ItemStack storage : entity.getInventory().getStorageContents()) {
						if (storage == null || storage.getType() == Material.AIR) {
							entity.getInventory().addItem(new ItemFactory(item).setDamage(damage).build());
							return;
						}
					}
					entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemFactory(item).setDamage(damage).build());
				}
//				entity.getInventory().addItem(new ItemStack(Material.STRING, 4));
				return;
			} else if (item.getType() != Material.AIR) {
				wool = item;
			}
		}

		if (shears == null || wool == null) {
			return;
		}

		event.setCurrentItem(null);
		event.setCancelled(true);
		event.setResult(Result.DENY);

		int craftCount = event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY ? wool.getAmount() : 1;
		int durability = shears.hasItemMeta() ? ((Damageable) shears.getItemMeta()).getDamage() : 0;
		int maxDurability = shears.getType().getMaxDurability() - 1;
		int shearsDamage = craftCount * 2;

		if (shearsDamage + durability > maxDurability) {
			craftCount = (maxDurability - durability) / 2;
			shearsDamage = maxDurability;
		}

		if (craftCount < 1) {
			craftCount = 1;
			shearsDamage = maxDurability;
		}

		int inventorySpace = 0;
		int craftedItemCount = craftCount * 4;
		for (ItemStack item : entity.getInventory().getStorageContents()) {
			if (item == null || item.getType() == Material.AIR) {
				inventorySpace += 64;
			} else if (item.getType() == Material.STRING) {
				inventorySpace += 64 - item.getAmount();
			}

			if (craftedItemCount < inventorySpace) {
				break;
			}
		}
		if (inventorySpace < craftedItemCount) {
			craftCount -= (craftedItemCount - inventorySpace) / 4;
			shearsDamage = craftCount * 2;
		}

		int unbreakingLevel = shears.getEnchantmentLevel(Enchantment.DURABILITY);
		if (unbreakingLevel > 0) {
			durability += shearsDamage - (shearsDamage / (int) ((unbreakingLevel + 1) * Math.random()));
		} else {
			durability += shearsDamage;
		}

		wool.setAmount(wool.getAmount() - craftCount);

		Inventory entityInventory = entity.getInventory();
		if (durability < maxDurability) {
			ItemMeta meta = shears.getItemMeta();
			((Damageable) meta).setDamage(durability);
			shears.setItemMeta(meta);
		} else {
			shears.setAmount(0);
		}

		event.setCurrentItem(null);
		craftCount = craftCount * 4;
		while (craftCount > 0) {
			if (craftCount > 63) {
				craftCount -= 64;
				entityInventory.addItem(new ItemStack(Material.STRING, 64));
				continue;
			}
			entityInventory.addItem(new ItemStack(Material.STRING, craftCount));
			craftCount = 0;
		}
	}

	@Override
	public NamespacedKey getNamespaceKey() {
		return this.getKey();
	}
}