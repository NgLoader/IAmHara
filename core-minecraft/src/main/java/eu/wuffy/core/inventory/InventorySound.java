package eu.wuffy.core.inventory;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class InventorySound {

	public static void playDenySound(Player player) {
		player.playSound(player.getEyeLocation(), Sound.ITEM_SHIELD_BREAK, 1, 0.6F);
	}

	public static void playSuccessSound(Player player) {
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1.6F);
	}

	public static void playToggleSound(Player player) {
		player.playSound(player.getEyeLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5F, 0.03F);
	}

	public static void playCloseSound(Player player) {
		player.playSound(player.getEyeLocation(), Sound.BLOCK_FENCE_GATE_OPEN, 24.01F, 0.000005F);
	}

	public static void playTeleportSound(Player player) {
		player.playSound(player.getEyeLocation(), Sound.ENTITY_SHULKER_TELEPORT, 22.01F, 0.0005F);
	}

	public static void playSelectSound(Player player) {
		player.playSound(player.getEyeLocation(), Sound.BLOCK_CHEST_CLOSE, 24.01F, 2.6F);
	}

	public static void playBackSound(Player player) {
		player.playSound(player.getEyeLocation(), Sound.BLOCK_CHEST_CLOSE, 24.01F, 2.6F);
	}

	public static void playOpenSound(Player player) {
		player.playSound(player.getEyeLocation(), Sound.BLOCK_CHEST_OPEN, 5.1F, 0.006F);
	}

	public static void playRemoveSound(Player player) {
		player.playSound(player.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 0.6F);
	}
}
