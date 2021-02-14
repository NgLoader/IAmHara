package eu.wuffy.survival.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.IHandler;

public class BoatHandler extends IHandler<Survival> implements Listener, Runnable {

	private final Random random = new Random();

	private final Set<Boat> boatsInUse = new HashSet<>();
	private final Map<Boat, Double> boatBoost = new HashMap<>();

	private Location boatLocation;
	private Location boatSpawnLocation;
	private Vector boatPushVelocity;
	private Boat currentBoat;

	public BoatHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.boatLocation = new Location(Bukkit.getWorld("world"), -272.5, 63, -48.5, 90, 0);
		this.boatSpawnLocation = new Location(Bukkit.getWorld("world"), -268.5, 63, -48.5, 90, 0);
		this.boatPushVelocity = this.boatLocation.toVector().subtract(this.boatSpawnLocation.toVector()).multiply(0.1D);

		Bukkit.getPluginManager().registerEvents(this, this.getCore());
		Bukkit.getScheduler().runTaskTimer(this.getCore(), this, 20 * 4, 20);
		Bukkit.getScheduler().runTaskTimer(this.getCore(), this::runBoatBoost, 20 * 4, 1);		
	}

	@Override
	public void onDisable() {
		if (this.currentBoat != null) {
			this.currentBoat.remove();
		}

		this.boatsInUse.forEach(Boat::remove);
		this.boatsInUse.clear();
	}

	@Override
	public void run() {
		if (this.currentBoat != null && !this.currentBoat.isDead()) {
			double distance = this.currentBoat.getLocation().distance(this.boatLocation);
			if (distance > 5) {
				this.spawnNewBoat(false);
			} else if (distance > 0.2 && this.currentBoat.getTicksLived() > 20) {
				this.currentBoat.setVelocity(this.boatLocation.toVector().subtract(this.currentBoat.getLocation().toVector()).normalize().multiply(0.075 * distance));
			}
		} else {
			this.spawnNewBoat(false);
		}
	}

	public void runBoatBoost() {
		Iterator<Map.Entry<Boat, Double>> iterator = this.boatBoost.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<Boat, Double> entry = iterator.next();
			Boat boat = entry.getKey();

			if (boat.isDead()) {
				iterator.remove();
				boat.remove();
				return;
			}

			if (boat.getLocation().distance(this.boatLocation) > 10) {
				iterator.remove();
				return;
			}

			double boost = entry.getValue();
			boat.setVelocity(this.boatPushVelocity.clone().multiply(boost));

			if (boost < 2) {
				entry.setValue(entry.getValue() + 0.025);
			}
		}
	}

	@EventHandler
	public void onEntityMount(EntityMountEvent event) {
		Entity mount = event.getMount();
		if (mount != null && this.currentBoat != null && mount.getUniqueId().equals(this.currentBoat.getUniqueId())) {
			if (event.getEntity() instanceof Player) {
				this.spawnNewBoat(true);
				this.boatBoost.put((Boat) mount, 0.1D);
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDismount(EntityDismountEvent event) {
		Entity mount = event.getDismounted();
		if (mount.getPassengers().size() != 2 && this.boatsInUse.remove(mount)) {
			mount.remove();
		}
	}

	@EventHandler
	public void onEntityDrop(EntityDropItemEvent event) {
		Entity entity = event.getEntity();
		if (this.boatsInUse.remove(entity)) {
			entity.remove();
			event.setCancelled(true);
			event.getItemDrop().remove();
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (this.boatsInUse.remove(entity)) {
			entity.remove();
			event.setDroppedExp(0);
			event.getDrops().clear();
		}
	}

	public void spawnNewBoat(boolean mounted) {
		if (!this.boatSpawnLocation.getChunk().isLoaded()) {
			return;
		}

		if (this.currentBoat != null) {
			if (this.currentBoat.getPassengers().isEmpty() && !mounted) {
				this.currentBoat.remove();
			} else {
				this.boatsInUse.add(this.currentBoat);
			}
		}

		this.currentBoat = (Boat) this.boatSpawnLocation.getWorld().spawnEntity(this.boatSpawnLocation, EntityType.BOAT);
		this.currentBoat.setWoodType(TreeSpecies.values()[this.random.nextInt(TreeSpecies.values().length)]);
		this.currentBoat.setVelocity(this.boatPushVelocity);
		this.currentBoat.setInvulnerable(true);
		this.currentBoat.setPersistent(true);
	}
}