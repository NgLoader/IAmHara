package eu.wuffy.core.inventory.inventory;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.inventory.InventoryClickable;
import eu.wuffy.core.inventory.listener.InventoryFilter;
import eu.wuffy.core.inventory.listener.InventoryListener.ListenerType;
import eu.wuffy.core.inventory.listener.ItemAction;

public class CustomInventory extends InventoryListenerAdapter {

	private final String title;

	private final int slots;

	private CustomInventory parent;

	private ItemStack[] content;
	private ItemAction[] actions;

	private InventoryFilter itemFilter;
	private InventoryClickable clickableTop = InventoryClickable.INVENTORY;
	private InventoryClickable clickableBottom = InventoryClickable.NOTHING;

	private Set<CustomInventoryPlayer> inventoryViewers = ConcurrentHashMap.newKeySet();

	public CustomInventory(String title, int rows) {
		this.title = title;

		this.slots = 9 * rows;
		this.content = new ItemStack[this.slots];
		this.actions = new ItemAction[this.slots];
	}

	public CustomInventory(String title, int rows, ItemStack[] content) {
		this.title = title;
		this.content = content;

		this.slots = 9 * rows;
		this.actions = new ItemAction[this.slots];
	}

	public CustomInventory(String title, int rows, ItemStack[] content, ItemAction[] actions) {
		this.title = title;
		this.content = content;
		this.actions = actions;

		this.slots = 9 * rows;
	}

	public CustomInventory(String title, int rows, ItemStack[] content, ItemAction[] actions, InventoryFilter itemFilter,
			InventoryClickable clickableTop, InventoryClickable clickableBottom) {
		this.title = title;
		this.content = content;
		this.actions = actions;
		this.itemFilter = itemFilter;
		this.clickableTop = clickableTop;
		this.clickableBottom = clickableBottom;

		this.slots = 9 * rows;
	}

	public void addViewer(CustomInventoryPlayer viewer) {
		this.inventoryViewers.add(viewer);
	}

	public void removeViewer(CustomInventoryPlayer viewer) {
		this.inventoryViewers.remove(viewer);
	}

	public void update() {
		this.checkViewers();
		this.inventoryViewers.forEach(CustomInventoryPlayer::updateContent);
	}

	public void update(InventoryView view) {
		this.checkViewers();
		this.inventoryViewers.forEach(viewer -> viewer.updateContent(view));
	}

	public void checkViewers() {
		for (Iterator<CustomInventoryPlayer> iterator = this.inventoryViewers.iterator(); iterator.hasNext(); ) {
			CustomInventoryPlayer viewer = iterator.next();
			if (viewer.getCustomInventory() != this || !viewer.getManager().hasInventory(viewer.getPlayer())) {
				iterator.remove();
			}
		}
	}

	public void setItemFilter(InventoryFilter itemFilter) {
		this.itemFilter = itemFilter;
	}

	public InventoryFilter getItemFilter() {
		return this.itemFilter;
	}

	public void setClickableTop(InventoryClickable clickableTop) {
		this.clickableTop = clickableTop;
	}
	
	public InventoryClickable getClickableTop() {
		return this.clickableTop;
	}
	
	public void setClickableBottom(InventoryClickable clickableBottom) {
		this.clickableBottom = clickableBottom;
	}
	
	public InventoryClickable getClickableBottom() {
		return this.clickableBottom;
	}

	public void setParent(CustomInventory parent) {
		this.parent = parent;
	}

	public ItemStack getItem(int slot) {
		return this.content[slot];
	}

	public ItemAction getAction(int slot) {
		return this.actions[slot];
	}

	public void setItems(ItemStack[] content) {
		this.content = content;
	}

	public CustomInventory setItem(int slot, ItemStack item) {
		this.content[slot] = item;
		return this;
	}

	public CustomInventory fill(ItemStack item) {
		for (int i = 0; i < this.content.length; i++) {
			this.content[i] = item;
		}
		return this;
	}

	public void setActions(ItemAction[] actions) {
		this.actions = actions;
	}

	public CustomInventory setAction(int slot, ItemAction action) {
		this.actions[slot] = action;
		return this;
	}

	public Set<CustomInventoryPlayer> getInventoryViewers() {
		this.checkViewers();
		return this.inventoryViewers;
	}

	public ItemAction[] getActions() {
		return this.actions;
	}

	public ItemStack[] getContent() {
		return this.content;
	}

	public CustomInventory getParent() {
		return this.parent;
	}

	public String getTitle() {
		return this.title;
	}

	public int getSlots() {
		return this.slots;
	}

	public CustomInventory clone() {
		CustomInventory customInventory = new CustomInventory(
				this.title,
				this.slots / 9,
				this.content.clone(),
				this.actions.clone(),
				this.itemFilter,
				this.clickableTop,
				this.clickableBottom);
		for (ListenerType listenerType : ListenerType.values()) {
			this.getListener(listenerType).forEach(listener -> customInventory.addListener(listenerType, listener));
		}
		return customInventory;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null) {
			if (obj instanceof CustomInventory) {
				CustomInventory inventory = (CustomInventory) obj;

				if (inventory.getTitle() == this.title &&
						inventory.getSlots() == this.slots &&
						inventory.getActions().equals(this.actions) &&
						inventory.getContent().equals(this.content) &&
						inventory.getItemFilter() == this.itemFilter) {
					return true;
				}
			}
		}

		return false;
	}
}
