package eu.wuffy.survival.warp;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Warp {

	public final int warpId;

	public final String name;

	public final String permission;

	public final String description;

	public final String world;
	public final double x, y, z;
	public final float yaw, pitch;
	public final Location location;

	public final List<WarpAlias> aliases;

	public Warp(int warpId, String name, String description, String permission, String world, double x, double y, double z, float yaw, float pitch, List<WarpAlias> aliases) {
		this.warpId = warpId;
		this.name = name;
		this.permission = permission != null ? permission : "";
		this.description = description != null ? description : "";
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.aliases = aliases;

		this.location = new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, this.yaw, this.pitch);
	}
}
