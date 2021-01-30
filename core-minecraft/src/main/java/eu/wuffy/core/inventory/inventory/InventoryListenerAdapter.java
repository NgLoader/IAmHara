package eu.wuffy.core.inventory.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.wuffy.core.inventory.listener.InventoryListener;
import eu.wuffy.core.inventory.listener.InventoryListener.ListenerType;

public class InventoryListenerAdapter {

	private Map<ListenerType, List<InventoryListener>> listeners = new HashMap<>();

	public void callListener(CustomInventoryPlayer customInventory, ListenerType type) {
		this.getListener(type).forEach(listener -> listener.on(customInventory));
	}

	public List<InventoryListener> getListener(ListenerType type) {
		return this.listeners.getOrDefault(type, Collections.emptyList());
	}

	public boolean addListener(ListenerType type, InventoryListener listener) {
		List<InventoryListener> listeners = this.listeners.get(type);
		if (listeners == null) {
			listeners = new ArrayList<>();
			this.listeners.put(type, listeners);
		}

		return listeners.add(listener);
	}

	public boolean removeListener(ListenerType type, InventoryListener listener) {
		List<InventoryListener> listeners = this.listeners.get(type);
		if (listeners == null) {
			return false;
		}

		return listeners.remove(listener);
	}
}
