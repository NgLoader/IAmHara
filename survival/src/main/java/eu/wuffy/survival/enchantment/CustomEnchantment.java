package eu.wuffy.survival.enchantment;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class CustomEnchantment extends Enchantment {

	private final String displayName;

	public CustomEnchantment(NamespacedKey key, String displayName) {
		super(key);

		this.displayName = displayName;
	}

	public void applyOn(ItemStack item, int level) {
		item.addUnsafeEnchantment(this, level);

		ItemMeta meta = item.getItemMeta();
		if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
			List<String> lores = new ArrayList<>();
			lores.add(ChatColor.GRAY + this.displayName + " " + this.getLevelName(level));

			if (meta.hasLore()) {
				for (String lore : meta.getLore()) {
					if (!lore.startsWith(ChatColor.GRAY + this.displayName + " ")) {
						lores.add(lore);
					}
				}
			}
			meta.setLore(lores);
			item.setItemMeta(meta);
		}
	}

	public boolean hasEnchant(ItemStack item) {
		return item != null && (item.containsEnchantment(this) || (item.hasItemMeta() && item.getItemMeta().hasEnchant(this)));
	}

	public int getEnchantLevel(ItemStack item) {
		return item != null && item.hasItemMeta() ? item.getItemMeta().getEnchantLevel(this) : 0;
	}

	public String getLevelName(int level) {
		switch (level) {
		case 1:
			return "I";
		case 2:
			return "II";
		case 3:
			return "III";
		case 4:
			return "IV";
		case 5:
			return "V";

		default:
			return String.valueOf(level);
		}
	}

	@Override
	public boolean conflictsWith(Enchantment other) {
		return false;
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

	@Override
	public String getName() {
		return this.displayName;
	}

	public String getDisplayName() {
		return this.displayName;
	}
}