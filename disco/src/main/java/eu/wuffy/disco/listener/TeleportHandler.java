package eu.wuffy.disco.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import eu.wuffy.core.util.ItemFactory;
import eu.wuffy.disco.Disco;
import eu.wuffy.synced.IHandler;

public class TeleportHandler extends IHandler<Disco> implements Listener {

	private final String inventoryName = "§6Teleporter";
	private final Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, this.inventoryName);

	private final Map<Integer, TeleportItem> teleportLocations = new HashMap<>();

	public TeleportHandler(Disco core) {
		super(core);

		ItemStack fillMaterial = new ItemFactory(Material.BLACK_STAINED_GLASS_PANE)
				.setDisplayName(" ")
				.addAllFlag()
				.build();

		for (int i = 0; i < this.inventory.getSize(); i++) {
			this.inventory.setItem(i, fillMaterial);
		}
	}

	@Override
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this.core);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& event.getItem() != null && event.getItem().getType() == Material.COMPASS) {
			event.setCancelled(true);

			event.getPlayer().openInventory(this.inventory);
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent event) {
		HumanEntity entity = event.getWhoClicked();

		if (event.getClickedInventory() != null && event.getClickedInventory().equals(this.inventory)) {
			event.setCancelled(true);

			TeleportItem teleportItem = this.teleportLocations.get(event.getSlot());
			if (teleportItem != null) {
				entity.teleport(teleportItem.getLocation());
				entity.sendMessage(Disco.PREFIX + "§7Du wurdest zu §a" + teleportItem.name + " §7teleportiert§8.");
			}
		}
	}

	public void addTeleport(String world, double x, double y, double z, float yaw, float pitch, ItemStack item,
			int slot, boolean enabled) {
		this.inventory.setItem(slot, item);

		if (enabled) {
			this.teleportLocations.put(slot,
					new TeleportItem(new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch), item, slot));
		}
	}

	private class TeleportItem {

		private final Location location;
		private final ItemStack item;

		private final String name;

		public TeleportItem(Location location, ItemStack item, int slot) {
			this.location = location;
			this.item = item;

			this.name = ChatColor.stripColor(this.item.getItemMeta().getDisplayName());
		}

		public Location getLocation() {
			return this.location;
		}
	}
}