package eu.wuffy.core.inventory.inventory.paginator;

import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.inventory.listener.ItemAction;

public class PageElement {

	private ItemStack item;
	private ItemAction action;

	private int slot = -1;
	private int page = -1;

	public PageElement(ItemStack item, ItemAction action) {
		this.item = item;
		this.action = action;
	}

	public ItemStack getItem() {
		return this.item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public ItemAction getAction() {
		return this.action;
	}

	public void setAction(ItemAction action) {
		this.action = action;
	}

	public int getSlot() {
		return this.slot;
	}
	
	public void setSlot(int slot) {
		this.slot = slot;
	}

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
