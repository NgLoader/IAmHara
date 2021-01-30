package eu.wuffy.core.inventory.inventory.paginator;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.inventory.InventorySound;
import eu.wuffy.core.inventory.inventory.CustomInventory;
import eu.wuffy.core.inventory.listener.InventoryListener.ListenerType;
import eu.wuffy.core.inventory.listener.ItemAction;
import eu.wuffy.core.util.ItemFactory;

public class CustomPaginatorInventory extends CustomInventory {

	private final PaginatorInventory paginator;
	private final int page;

	private int nextSlot = 10;
	private List<PageElement> items = new ArrayList<>();

	protected ItemStack inventoryBackground;
	protected ItemStack inventorySlotHolder;

	public CustomPaginatorInventory(PaginatorInventory paginator, int page, String name, List<PageElement> items) {
		super(name, 6);
		this.paginator = paginator;
		this.page = page;
		this.items = items;

		this.inventoryBackground = this.paginator.inventoryBackground;
		this.inventorySlotHolder = this.paginator.inventorySlotHolder;

		this.addListener(ListenerType.OPEN, (action) -> InventorySound.playSelectSound(action.getPlayer()));
		this.build();
	}

	public void build() {
		this.fill(this.inventoryBackground);
		this.setItem(4, new ItemFactory(Material.BOOK)
				.setDisplayName("§aSeite §7" + this.page)
				.setNumber(this.page < 1 ? 1 : this.page < 64 ? this.page : 64)
				.addAllFlag()
				.build());

		this.reorderItems();

		if (page > 1) {
			this.setItem(49, new ItemFactory(Material.BARRIER).setDisplayName("§cSeite 0").addAllFlag().build());
			this.setAction(49, ItemAction.open(this.paginator.getPage(0)));
		} else if (page == 0) {
			if (this.paginator.parentItem != null && this.paginator.parentInventory != null) {
				this.setItem(46, this.paginator.parentItem);
				this.setAction(46, ItemAction.open(this.paginator.parentInventory));
			} else {
				this.setItem(46, this.inventoryBackground);
				this.setAction(46, null);
			}
		}
		this.update();
	}

	public void reorderItems() {
		this.nextSlot = 10;
		for (PageElement element : this.items) {
			element.setPage(this.page);
			element.setSlot(this.nextSlot);
			this.setItem(this.nextSlot, element.getItem());
			this.setAction(this.nextSlot, element.getAction());

			this.nextSlot();
		}

		for (int i = nextSlot; i < 35; i++) {
			if (i % 9 != 0 && (i + 1) % 9 != 0) {
				this.setItem(i, this.inventorySlotHolder);
				this.setAction(i, null);
			}
		}
	}

	public PageElement addItem(PageElement element) {
		this.updateElement(element, this.nextSlot);
		this.items.add(element);

		this.setItem(this.nextSlot, element.getItem());
		this.setAction(this.nextSlot, element.getAction());
		this.nextSlot();
		return element;
	}

	public void removeItem(PageElement element) {
		this.items.remove(element);
		this.reorderItems();
	}

	public void removeAndAddItem(PageElement removeElement, PageElement addElement) {
		this.items.remove(removeElement);
		this.items.add(addElement);
		this.trashElement(removeElement);
		this.reorderItems();
	}

	public void replaceItem(PageElement oldElement, PageElement newElement) {
		int index = this.items.indexOf(oldElement);

		if (index != -1) {
			this.items.set(index, newElement);
		} else {
			this.items.remove(oldElement);
			this.items.add(newElement);
		}

		int slot = oldElement.getSlot();
		this.updateElement(newElement, slot);
		this.trashElement(oldElement);

		this.setItem(slot, newElement.getItem());
		this.setAction(slot, newElement.getAction());
	}

	private void updateElement(PageElement element, int slot) {
		element.setPage(this.page);
		element.setSlot(slot);
	}

	private void trashElement(PageElement element) {
		element.setPage(-1);
		element.setSlot(-1);
	}

	private void nextSlot()  {
		do {
			this.nextSlot++;
		} while (this.nextSlot % 9 == 0 || (this.nextSlot + 1) % 9 == 0);
	}

	public PageElement getPageElement(int slot) {
		return this.items.stream().filter(item -> item.getSlot() == slot).findFirst().orElse(null);
	}

	public List<PageElement> getItems() {
		return this.items;
	}
}