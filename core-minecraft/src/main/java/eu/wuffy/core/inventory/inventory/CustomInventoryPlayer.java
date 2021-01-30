package eu.wuffy.core.inventory.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.inventory.InventorySystem;
import eu.wuffy.core.inventory.listener.InventoryListener.ListenerType;

public class CustomInventoryPlayer extends InventoryListenerAdapter {

	private final InventorySystem manager;

	private final Player player;

	private CustomInventory currentInventory;
	private Inventory inventory;

	private String inventoryTitle = "";

	public CustomInventoryPlayer(InventorySystem manager, Player player) {
		this.manager = manager;
		this.player = player;
	}

	public CustomInventoryPlayer(InventorySystem manager, Player player, CustomInventory inventory) {
		this(manager, player);

		this.open(inventory);
	}

	public void open(CustomInventory inventory) {
		this.open(inventory, null);
	}

	public void open(CustomInventory inventory, InventoryView view) {
		if (this.currentInventory != null) {
			inventory.setParent(this.currentInventory);
			this.currentInventory.callListener(this, ListenerType.CLOSE);
			this.currentInventory.removeViewer(this);
		} else {
			this.callListener(this, ListenerType.OPEN);
		}

		this.currentInventory = inventory;
		this.currentInventory.addViewer(this);
		this.currentInventory.callListener(this, ListenerType.OPEN);
		this.updateContent(view);
	}

	public void openParent() {
		this.openParent(null);
	}

	public void openParent(InventoryView view) {
		if (this.currentInventory == null) {
			return;
		}

		CustomInventory parent = this.currentInventory.getParent();
		if (parent == null) {
			this.manager.closeInventory(this.player);
			return;
		}

		this.currentInventory.callListener(this, ListenerType.CLOSE);
		this.currentInventory.removeViewer(this);
		this.currentInventory = parent;
		this.currentInventory.addViewer(this);
		this.currentInventory.callListener(this, ListenerType.OPEN);
		this.updateContent(view);
	}

	public void updateContent() {
		this.updateContent(null);
	}

	public void updateContent(InventoryView view) {
		if (this.updateInventory(view)) {
			this.inventory.setContents(this.currentInventory.getContent());
			this.player.openInventory(this.inventory);
		} else {
			ItemStack[] newContent = this.currentInventory.getContent();
			ItemStack[] currentContent = this.inventory.getContents();

			if (currentContent.length != newContent.length) {
				this.inventory.setContents(newContent);
				return;
			}

			for (int i = 0; i < newContent.length; i++) {
				ItemStack newItemStack = newContent[i];
				ItemStack currentItemStack = currentContent[i];
				if ((newItemStack == null && currentItemStack != null) || !newItemStack.equals(currentItemStack)) {
					this.inventory.setItem(i, newItemStack);
				}
			}
		}
	}

	private boolean updateInventory(InventoryView view) {
		int slots = this.currentInventory.getSlots();
		String title = this.currentInventory.getTitle();

		if (this.inventory != null && this.inventory.getSize() == slots && (view != null ? view.getTitle() : this.inventoryTitle).equals(title)) {
			return false;
		}

		this.inventoryTitle = title;
		this.inventory = Bukkit.createInventory(null, slots, title);
		return true;
	}

	public void closeInventory() {
		this.manager.closeInventory(this.player);
	}

	public void destroy() {
		if (this.currentInventory != null) {
			this.callListener(this, ListenerType.CLOSE);
			this.currentInventory.callListener(this, ListenerType.CLOSE);
			this.currentInventory.removeViewer(this);
			this.currentInventory = null;
		}

		this.inventory.clear();
		this.inventory = null;
	}

	public InventorySystem getManager() {
		return this.manager;
	}

	public CustomInventory getCustomInventory() {
		return this.currentInventory;
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public Player getPlayer() {
		return this.player;
	}
}