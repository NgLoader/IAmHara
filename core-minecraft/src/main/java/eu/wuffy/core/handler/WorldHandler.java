package eu.wuffy.core.handler;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.WorldCreator;

import eu.wuffy.core.Core;
import eu.wuffy.core.database.CoreDatabase;
import eu.wuffy.synced.IHandler;

public class WorldHandler extends IHandler<Core<CoreDatabase>> {

	private final Map<String, World> worlds = new HashMap<>();

	public WorldHandler(Core<CoreDatabase> core) {
		super(core);
	}

	public void loadConfig() {
		
	}

	public void saveConfig() {
		
	}

//	public World createWorld(String worldName, Environment environment) {

	public World loadWorld(String worldName) {
		World world = this.worlds.get(worldName);

		if (world == null) {
			new WorldCreator(worldName).createWorld();
		}

		return world;
	}

	public void unloadWorld(World world) {
		
	}

	public void removeWorld(World world) {
	}
}