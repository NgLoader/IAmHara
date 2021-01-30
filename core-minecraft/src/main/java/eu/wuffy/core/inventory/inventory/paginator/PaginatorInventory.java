package eu.wuffy.core.inventory.inventory.paginator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.inventory.InventoryItem;
import eu.wuffy.core.inventory.inventory.CustomInventory;
import eu.wuffy.core.inventory.inventory.CustomInventoryPlayer;
import eu.wuffy.core.inventory.listener.ItemAction;
import eu.wuffy.core.util.ItemFactory;

public class PaginatorInventory {

	protected final String name;
	protected final List<PageElement> items;

	protected ItemStack inventoryBackground = InventoryItem.DEFAULT_BACKGROUND.build();
	protected ItemStack inventorySlotHolder = InventoryItem.DEFAULT_SLOT_HOLDER.build();
	protected ItemFactory inventoryArrowRight = InventoryItem.DEFAULT_ARROW_RIGHT;
	protected ItemFactory inventoryArrowLeft = InventoryItem.DEFAULT_ARROW_LEFT;

	protected ItemStack parentItem;
	protected CustomInventory parentInventory;

	private CustomPaginatorInventory[] pages;

	public PaginatorInventory(String name) {
		this(name, new ArrayList<>());
	}

	public PaginatorInventory(String name, List<PageElement> items) {
		this.name = name;
		this.items = items;

		this.pages = new CustomPaginatorInventory[ 1 + (items.size() / 21)];
		this.generatePage(0);
	}

	public void setParent(CustomInventory parentInventory, ItemStack parentItem) {
		this.parentInventory = parentInventory;
		this.parentItem = parentItem;

		if (this.parentItem == null) {
			this.parentItem = InventoryItem.DEFAULT_ARROW_LEFT.clone()
					.setDisplayName("§aZurück")
					.build();
		}
		this.pages[0].build();
	}

	public void addItem(PageElement item) {
		this.items.add(item);

		int lastPage = this.pages.length - 1;
		CustomPaginatorInventory inventory = this.pages[lastPage];
		if (inventory.getItems().size() > 20) {
			inventory.setItem(52, this.getArrowRight(lastPage + 1));
			inventory.setAction(52, ItemAction.open(this.generatePage(this.pages.length)));
		} else {
			item.setPage(lastPage);
			inventory.addItem(item);
		}
		inventory.update();
	}

	public void removeItem(PageElement item) {
		this.items.remove(item);

		if (item.getSlot() != -1 && item.getPage() != -1) {
			CustomPaginatorInventory inventory = null;
			PageElement moveItem = null;
			for (int i = this.pages.length - 1; i > -1; i--) {
				inventory = this.pages[i];
				if (moveItem == null) {
					if (i == item.getPage()) {
						inventory.removeItem(item);

						if (inventory.getItems().isEmpty() && i != 0) {
							this.setBackground(i - 1, 52);
							this.pages = Arrays.copyOfRange(this.pages, 0, this.pages.length - 1);
							inventory.getInventoryViewers().forEach(CustomInventoryPlayer::openParent);
							break;
						}
						inventory.update();
						break;
					}

					moveItem = inventory.getPageElement(10);
					inventory.removeItem(moveItem);

					if (inventory.getItems().isEmpty() && i != 0) {
						this.setBackground(i - 1, 52);
						this.pages = Arrays.copyOfRange(this.pages, 0, this.pages.length - 1);
						inventory.getInventoryViewers().forEach(CustomInventoryPlayer::openParent);
						continue;
					}
					inventory.update();
					continue;
				}

				if (item.getPage() == i) {
					inventory.removeAndAddItem(item, moveItem);
					inventory.update();
					break;
				}

				PageElement wrapper = inventory.getPageElement(10);
				inventory.removeAndAddItem(wrapper, moveItem);
				moveItem = wrapper;
				inventory.update();
			}
		}
	}

	public CustomPaginatorInventory generatePage(int page) {
		ArrayList<PageElement> items = new ArrayList<>();
		boolean nextPage = false;
		for (PageElement item : this.items) {
			if (item.getSlot() == -1) {
				items.add(item);

				if (items.size() > 21) {
					items.remove(item);
					nextPage = true;
					break;
				}

				item.setPage(page);
			}
		}

		CustomPaginatorInventory inventory = new CustomPaginatorInventory(this, page, this.name, items);
		if (this.pages.length - 1 < page) {
			this.pages = Arrays.copyOf(this.pages, this.pages.length + 1);
		}
		this.pages[page] = inventory;

		if (nextPage) {
			inventory.setItem(52, this.getArrowRight(page + 1));
			inventory.setAction(52, ItemAction.open(this.generatePage(page + 1)));
		}
		if (page != 0) {
			inventory.setItem(46, this.getArrowLeft(page - 1));
			inventory.setAction(46, ItemAction.openParent());
		}
		return inventory;
	}

	private void setBackground(int page, int slot) {
		this.pages[page].setItem(slot, this.pages[page].inventoryBackground);
		this.pages[page].setAction(slot, null);
		this.pages[page].update();
	}

	public CustomPaginatorInventory getPage(int page) {
		return this.pages.length < page ? null : this.pages[page];
	}

	public ItemStack getArrowRight(int page) {
		return this.inventoryArrowRight.clone().setDisplayName(this.inventoryArrowRight.getDisplayName().replace("%p", String.valueOf(page))).build();
	}

	public ItemStack getArrowLeft(int page) {
		return this.inventoryArrowLeft.clone().setDisplayName(this.inventoryArrowLeft.getDisplayName().replace("%p", String.valueOf(page))).build();
	}

	public List<PageElement> getItems() {
		return this.items;
	}

	public String getName() {
		return this.name;
	}
}