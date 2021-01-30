package eu.wuffy.core.inventory;

public enum InventoryClickable {

	/**
	 * Its able to interact with items
	 */
	INVENTORY,

	/**
	 * Its only able to interact with items when the filter return true
	 */
	FILTER,

	/**
	 * Its not able to move items
	 */
	NOTHING
}