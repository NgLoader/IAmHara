package eu.wuffy.lobby.handler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.lobby.Lobby;
import eu.wuffy.synced.IHandler;

public class InventoryHandler extends IHandler<Lobby> implements Listener {

	private final ItemStack[] items = new ItemStack[8];

	public InventoryHandler(Lobby core) {
		super(core);

		this.items[4] = new ItemFactory(Material.COMPASS).setDisplayName("§2Teleporter").addAllFlag().build();
	}

	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this.core);
	}

	public ItemStack[] getInventory() {
		return this.items;
	}
}