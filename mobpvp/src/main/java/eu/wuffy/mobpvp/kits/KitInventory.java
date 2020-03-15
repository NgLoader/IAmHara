package eu.wuffy.mobpvp.kits;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class KitInventory {

	private ItemStack[] storageInventory = new ItemStack[32];
	private ItemStack[] armorInventory = new ItemStack[4];
	private ItemStack[] extraInventory = new ItemStack[1];

	public void setStorageItem(int slot, ItemStack item) {
		if (slot < 0 || slot > this.storageInventory.length - 1) {
			throw new IllegalStateException("Slot: " + slot + " is out of range! (Storage) Item: " + item.toString());
		}

		this.storageInventory[slot] = item;
	}

	public void setArmorItem(int slot, ItemStack item) {
		if (slot < 0 || slot > this.armorInventory.length - 1) {
			throw new IllegalStateException("Slot: " + slot + " is out of range! (Armor) Item: " + item.toString());
		}

		this.armorInventory[slot] = item;
	}

	public void setExtraItem(int slot, ItemStack item) {
		if (slot < 0 || slot > this.extraInventory.length - 1) {
			throw new IllegalStateException("Slot: " + slot + " is out of range! (Extra) Item: " + item.toString());
		}

		this.extraInventory[slot] = item;
	}

	public void apply(PlayerInventory inventory) {
		inventory.setStorageContents(this.storageInventory);
		inventory.setArmorContents(this.armorInventory);
		inventory.setExtraContents(this.extraInventory);
	}

	public ItemStack[] getStorageInventory() {
		return this.storageInventory;
	}

	public ItemStack[] getArmorInventory() {
		return this.armorInventory;
	}

	public ItemStack[] getExtraInventory() {
		return this.extraInventory;
	}
}