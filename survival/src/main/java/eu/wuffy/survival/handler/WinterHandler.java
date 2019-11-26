package eu.wuffy.survival.handler;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.IHandler;

public class WinterHandler extends IHandler<Survival> {

	private List<Location> lampLocations = new ArrayList<Location>();

	public WinterHandler(Survival core) {
		super(core);
	}

	@Override
	public void init() {
		World world = Bukkit.getWorld("world");
		lampLocations.add(new Location(world, -2109, 104, -4220));
		lampLocations.add(new Location(world, -2110, 104, -4219));
		lampLocations.add(new Location(world, -2111, 104, -4218));
		lampLocations.add(new Location(world, -2112, 104, -4219));
		lampLocations.add(new Location(world, -2113, 104, -4220));
		lampLocations.add(new Location(world, -2112, 104, -4221));
		lampLocations.add(new Location(world, -2111, 104, -4222));
		lampLocations.add(new Location(world, -2110, 104, -4221));
		lampLocations.add(new Location(world, -2110, 105, -4220));
		lampLocations.add(new Location(world, -2111, 105, -4219));
		lampLocations.add(new Location(world, -2112, 105, -4220));
		lampLocations.add(new Location(world, -2111, 105, -4221));
		lampLocations.add(new Location(world, -2110, 106, -4220));
		lampLocations.add(new Location(world, -2111, 106, -4219));
		lampLocations.add(new Location(world, -2112, 106, -4220));
		lampLocations.add(new Location(world, -2111, 106, -4221));
		lampLocations.add(new Location(world, -2111, 107, -4220));
	}

	@Override
	public void onEnable() {
		this.lampLocations.forEach(lamp -> this.updateLamp(lamp, true));
	}

	public void updateLamp(Location location, boolean lampOn) {
	}
}