package eu.wuffy.core.inventory.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import eu.wuffy.core.inventory.InventorySound;
import eu.wuffy.core.inventory.inventory.CustomInventoryPlayer;

public interface InventoryListener {

	public static InventoryListener playSound(Sound sound, float volume, float pitch) {
		return (inventory) -> {
			Player player = inventory.getPlayer();
			player.playSound(player.getEyeLocation(), sound, volume, pitch);
		};
	}

	public static InventoryListener playCloseSound() {
		return (inventory) -> {
			InventorySound.playCloseSound(inventory.getPlayer());
		};
	}

	public static InventoryListener playOpenSound() {
		return (inventory) -> {
			InventorySound.playOpenSound(inventory.getPlayer());
		};
	}

	void on(CustomInventoryPlayer inventory);

	public enum ListenerType {
		OPEN,
		CLOSE,
		CLICKED,
		ITEM_FILTER_SUCCESS,
		ITEM_FILTER_FAILED;
	}
}