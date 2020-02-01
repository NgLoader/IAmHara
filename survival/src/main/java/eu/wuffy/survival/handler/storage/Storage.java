package eu.wuffy.survival.handler.storage;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.Inventory;

public class Storage {

	private final int id;
	private final Map<Integer, Inventory> inventorys = new HashMap<Integer, Inventory>();

	public Storage(int id) {
		this.id = id;
	}

	public Inventory getInventory(int page) {
		return this.inventorys.get(page);
	}

	public int getId() {
		return this.id;
	}
}