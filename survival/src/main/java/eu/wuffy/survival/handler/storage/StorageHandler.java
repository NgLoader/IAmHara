package eu.wuffy.survival.handler.storage;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.IHandler;

public class StorageHandler extends IHandler<Survival> {

	public Map<Location, Storage> storages = new HashMap<Location, Storage>();

	public StorageHandler(Survival core) {
		super(core);
	}

	public Storage getStorage(Location location) {
		return this.storages.get(location);
	}

	public Storage createStorage(Location location, int pages) {
		Storage storage = new Storage(0);

		this.storages.put(location, storage);
		return storage;
	}
}