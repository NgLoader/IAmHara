package eu.wuffy.survival.handler.home;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Home {

	public final int homeId;

	public final String name;
	public final String description;

	public final String world;
	public final double x, y, z;
	public final float yaw, pitch;
	public final Location location;

	public Home(int homeId, String name, String description, String world, double x, double y, double z, float yaw, float pitch) {
		this.homeId = homeId;
		this.name = name;
		this.description = description;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;

		this.location = new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, this.yaw, this.pitch);
	}
}