package eu.wuffy.disco.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.disco.Disco;
import eu.wuffy.synced.IHandler;
import fr.cocoraid.prodigynightclub.utils.Head;

public class InventoryHandler extends IHandler<Disco> implements Listener {

	private static final ItemStack[] EMPTY_ITEMS = new ItemStack[0];

	private final Map<InventoryEnum, ItemStack[]> items = new HashMap<>();

	public InventoryHandler(Disco core) {
		super(core);

		this.createInventory(InventoryEnum.DEFAULT, 8);
		this.addItem(InventoryEnum.DEFAULT, new ItemFactory(Material.COMPASS).setDisplayName("§2Teleporter").addAllFlag().build(), 4);

		this.createInventory(InventoryEnum.ADMIN_IN_DISCO, 8);
		this.addItem(InventoryEnum.ADMIN_IN_DISCO, new ItemFactory(Material.COMPASS).setDisplayName("§2Teleporter").addAllFlag().build(), 2);
		this.addItem(InventoryEnum.ADMIN_IN_DISCO, Head.MENU.getHead(), 6);
	}

	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this.core);
	}

	public void createInventory(InventoryEnum inventory, int slots) {
		this.items.put(inventory, new ItemStack[slots]);
	}

	public void addItem(InventoryEnum inventory, ItemStack item, int slot) {
		ItemStack[] items = this.items.get(inventory);

		if (items == null || slot < 0 || items.length - 1 < slot) {
			return;
		}

		items[slot] = item;
	}

	public ItemStack[] getInventory(InventoryEnum inventory) {
		return this.items.getOrDefault(inventory, EMPTY_ITEMS);
	}

	public enum InventoryEnum {
		DEFAULT,
		ADMIN_IN_DISCO
	}
}