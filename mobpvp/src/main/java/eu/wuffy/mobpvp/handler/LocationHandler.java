package eu.wuffy.mobpvp.handler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.synced.IHandler;

public class LocationHandler extends IHandler<MobPvP> {

	/*
	 * Spawn
	 * Map
	 */

	private Map<String, List<Location>> locationsByName = new HashMap<String, List<Location>>();

	public LocationHandler(MobPvP core) {
		super(core);
	}

	@Override
	public void onEnable() {
		// TODO add default locations!
		this.addLoaction("spawn", new Location(Bukkit.getWorld("world"), 441, 81.1, -392, 180f, 180f));
		this.addLoaction("map", new Location(Bukkit.getWorld("world"), 444, 81.1, -392, 180f, 180f));
		this.addLoaction("map", new Location(Bukkit.getWorld("world"), 436, 81.1, -392, 180f, 180f));

		// TODO load locations in a better way
		try {
			Path src = Paths.get("world/mobpvp.properties");

			if (!Files.exists(src)) {
				Files.createFile(src);
			}

			Properties properties = new Properties();

			try (InputStream inputStream = Files.newInputStream(src)) {
				properties.load(inputStream);
			}

			int count = 0;
			String property = null;
			while ((property = properties.getProperty("location-" + count + "-x", null)) != null) {
				try {
					int x = Integer.valueOf(property);
					int y = Integer.valueOf(properties.getProperty("location-" + count + "-y", null));
					int z = Integer.valueOf(properties.getProperty("location-" + count + "-z", null));

					Location location = new Location(Bukkit.getWorld(properties.getProperty("location-" + count + "-world")), x, y, z);
					String yaw = properties.getProperty("location-" + count + "-yaw", null);
					String pitch = properties.getProperty("location-" + count + "-pitch", null);

					if (yaw != null) {
						location.setYaw(Float.valueOf(yaw));
					}
					if (pitch != null) {
						location.setPitch(Float.valueOf(pitch));
					}
				} catch(NumberFormatException e) {
					e.printStackTrace();
				}

				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean addLoaction(String locationName, Location location) {
		locationName = locationName.toLowerCase();
		List<Location> locations = this.locationsByName.get(locationName);

		if (locations == null) {
			locations = new ArrayList<Location>();
			locations.add(location);

			this.locationsByName.put(locationName, locations);
			return true;
		}

		if (!locations.contains(location)) {
			locations.add(location);
			return true;
		}
		return false;
	}

	public boolean removeLocation(String locationName, Location location) {
		locationName = locationName.toLowerCase();
		List<Location> locations = this.locationsByName.get(locationName);

		if (locations != null) {
			if (locations.contains(location)) {
				locations.remove(location);

				if (locations.size() < 1) {
					this.locationsByName.remove(locationName);
				}
				return true;
			}
		}
		return false;
	}

	public void teleport(Player player, String locationName) {
		List<Location> locations = this.locationsByName.get(locationName.toLowerCase());

		if (locations != null && locations.size() > 0) {
			player.teleport(locations.get(locations.size() < 2 ? 0 : RandomUtils.nextInt(locations.size())));
		}

		// TODO send message that no location was found
	}

	public Location get(String locationName) {
		List<Location> locations = this.locationsByName.get(locationName.toLowerCase());

		if (locations != null && locations.size() > 0) {
			return locations.get(locations.size() < 2 ? 0 : RandomUtils.nextInt(locations.size()));
		}

		return null;
	}
}