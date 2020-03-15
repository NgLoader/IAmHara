package eu.wuffy.disco.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import eu.wuffy.disco.Disco;
import eu.wuffy.synced.IHandler;

public class DisallowHandler extends IHandler<Disco> implements Listener {

	private final BuildHandler buildHandler;

	private Location spawnLocation;

	public DisallowHandler(Disco core) {
		super(core);
		this.buildHandler = this.core.getBuildHandler();
	}

	@Override
	public void onEnable() {
		this.spawnLocation = Bukkit.getWorld("world").getSpawnLocation();
		this.spawnLocation.setYaw(90);

		Bukkit.getServer().getPluginManager().registerEvents(this, this.core);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		if (this.buildHandler.isInBuildMode(event.getPlayer())) {
			event.setCancelled(false);
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (this.buildHandler.isInBuildMode(event.getPlayer())) {
			event.setCancelled(false);
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (this.buildHandler.isInBuildMode(event.getPlayer())) {
			event.setCancelled(false);
			return;
		}

		event.setCancelled(true);
	}

	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onEntityDropItem(PlayerDropItemEvent event) {
		if (this.buildHandler.isInBuildMode(event.getPlayer())) {
			event.setCancelled(false);
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onEntityPickupItem(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player && this.buildHandler.isInBuildMode((Player) event.getEntity())) {
			event.setCancelled(false);
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.setJoinMessage(null);

		player.setGameMode(GameMode.ADVENTURE);
		player.teleport(this.spawnLocation, TeleportCause.PLUGIN);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			if (this.buildHandler.isInBuildMode((Player) event.getWhoClicked())) {
				event.setCancelled(false);
				return;
			}
		}

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.NORMAL)
	public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
		event.setSpawnLocation(this.spawnLocation);
	}
}