package eu.wuffy.core.hologram;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Hologram {

	private final int id;

	private ArmorStand armorStand;

	private Location location;
	private String text;

	public Hologram(int id, Location location, String text) {
		this.id = id;
		this.location = location;
		this.text = text;
	}

	public void spawn() {
		this.despawn();

		this.armorStand = (ArmorStand) this.location.getWorld().spawnEntity(this.location, EntityType.ARMOR_STAND);

		this.armorStand.setVisible(false);
		this.armorStand.setCustomName(this.text);
		this.armorStand.setCustomNameVisible(true);
		this.armorStand.setGravity(false);
		this.armorStand.setInvulnerable(true);
		this.armorStand.setCanPickupItems(false);
		this.armorStand.setSilent(true);
		this.armorStand.setSmall(true);
	}

	public void despawn() {
		if (this.armorStand != null) {
			this.armorStand.remove();
			this.armorStand = null;
		}
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;

		if (this.armorStand != null) {
			this.armorStand.teleport(this.location);
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;

		if (this.armorStand != null) {
			this.armorStand.setCustomName(this.text);
		}
	}

	public int getId() {
		return this.id;
	}
}