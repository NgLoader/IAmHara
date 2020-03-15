package eu.wuffy.survival.handler.tpa;

import org.bukkit.Location;

public class TPAInfo {

	public final Long expired;
	public final Location location;

	public TPAInfo(Long expired, Location location) {
		this.expired = expired;
		this.location = location;
	}
}