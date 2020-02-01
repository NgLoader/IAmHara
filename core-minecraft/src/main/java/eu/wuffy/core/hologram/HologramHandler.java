package eu.wuffy.core.hologram;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

import eu.wuffy.core.Core;
import eu.wuffy.synced.IHandler;

public class HologramHandler extends IHandler<Core<?>> {

	private Map<Integer, Hologram> holograms = new HashMap<>();

	public HologramHandler(Core<?> core) {
		super(core);
	}

	@Override
	public void onDisable() {
		this.despawnAll();
		this.holograms.clear();
	}

	public void spawnAll() {
		this.holograms.values().forEach(Hologram::spawn);
	}

	public void despawnAll() {
		this.holograms.values().forEach(Hologram::despawn);
	}

	public int addHologram(Location location, String text) {
		int id = this.holograms.size();
		this.holograms.put(id, new Hologram(id, location, text));

		return id;
	}

	public Hologram getHologram(int id) {
		return this.holograms.get(id);
	}
}