package eu.wuffy.core.inventory.listener;

import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import eu.wuffy.core.inventory.inventory.CustomInventory;
import eu.wuffy.core.inventory.inventory.CustomInventoryPlayer;

public interface ItemAction {

	/**
	* Item is movable
	 * 
	 * @param staticAction
	 * @return
	 */
	public static ItemAction movable(ItemAction.StaticAction staticAction) {
		return (playerInventory, inventory, action, event) -> {
			staticAction.onAction(playerInventory, inventory, action, event);
			return ReturnType.MOVABLE;
		};
	}

	/**
	* Item is not movable
	 * 
	 * @param staticAction
	 * @return
	 */
	public static ItemAction immovable(ItemAction.StaticAction staticAction) {
		return (playerInventory, inventory, action, event) -> {
			staticAction.onAction(playerInventory, inventory, action, event);
			return ReturnType.IMMOVABLE;
		};
	}

	/**
	* Item can be removed
	 * 
	 * @param staticAction
	 * @return
	 */
	public static ItemAction removeable(ItemAction.StaticAction staticAction) {
		return (playerInventory, inventory, action, event) -> {
			staticAction.onAction(playerInventory, inventory, action, event);
			return ReturnType.REMOVEABLE;
		};
	}

	/**
	 * Item can be placed
	 * 
	 * @param staticAction
	 * @return
	 */
	public static ItemAction placeable(ItemAction.StaticAction staticAction) {
		return (playerInventory, inventory, action, event) -> {
			staticAction.onAction(playerInventory, inventory, action, event);
			return ReturnType.PLACEABLE;
		};
	}

	/**
	 * Open the parent inventory
	 * When no parent inventory exist the current inventory will be closed
	 * 
	 * @return {@link ItemAction}
	 */
	public static ItemAction openParent() {
		return (playerInventory, inventory, action, event) -> {
			playerInventory.openParent();
			return ReturnType.IMMOVABLE;
		};
	}

	/**
	 * Open a new inventory
	 * The old inventory will be set as parent in the new inventory!
	 * 
	 * @param customInventory
	 * @return {@link ItemAction}
	 */
	public static ItemAction open(CustomInventory customInventory) {
		return (playerInventory, inventory, action, event) -> {
			playerInventory.open(customInventory);
			return ReturnType.IMMOVABLE;
		};
	}

	/**
	 * Close the inventory
	 * 
	 * @return {@link ItemAction}
	 */
	public static ItemAction close() {
		return (playerInventory, inventory, action, event) -> {
			playerInventory.closeInventory();
			return ReturnType.IMMOVABLE;
		};
	}

	ReturnType onAction(CustomInventoryPlayer playerInventory, CustomInventory inventory, InventoryAction action, InventoryClickEvent event);

	public static interface StaticAction {

		void onAction(CustomInventoryPlayer playerInventory, CustomInventory inventory, InventoryAction action, InventoryClickEvent event);
	}

	public enum ReturnType {
		/**
		 * Item is movable
		 */
		MOVABLE,

		/**
		 * Item is not movable
		 */
		IMMOVABLE,

		/**
		 * Item can be removed
		 */
		REMOVEABLE,

		/**
		 * Item can be placed
		 */
		PLACEABLE
	}
}