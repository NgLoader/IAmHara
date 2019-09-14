package eu.wuffy.survival.home;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import eu.wuffy.core.IHandler;
import eu.wuffy.survival.Survival;

public class HomeHandler extends IHandler<Survival> {

	private Map<UUID, List<Home>> homes = new HashMap<UUID, List<Home>>();

	public HomeHandler(Survival core) {
		super(core);
	}

	@Override
	public void onInit() { }

	@Override
	public void onEnable() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			try {
				this.load(player.getUniqueId());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void onDisable() { }

	public void load(UUID uuid) throws SQLException {
		if (this.homes.containsKey(uuid))
			this.homes.get(uuid).clear();
		else
			this.homes.put(uuid, new ArrayList<Home>());

		this.getCore().getDatabase().loadHomes(uuid).forEach(home -> this.homes.get(uuid).add(home));
	}

	public void unload(UUID uuid) {
		if (this.homes.containsKey(uuid))
			this.homes.remove(uuid);
	}

	public Home get(UUID uuid, String name) {
		if (!this.homes.containsKey(uuid))
			try {
				this.load(uuid);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		Optional<Home> found = this.homes.get(uuid).stream().filter(home -> home.name.equalsIgnoreCase(name)).findFirst();

		if(found.isPresent())
			return found.get();
		return null;
	}

	public boolean exist(UUID uuid, String name) {
		return this.homes.containsKey(uuid) && homes.get(uuid).stream().filter(home -> home.name.equalsIgnoreCase(name)).findAny().isPresent();
	}

	public Home create(UUID uuid, String name, String description, Location location) throws SQLException {
		Home home = this.getCore().getDatabase().createHome(uuid, name, description, location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

		if (!this.homes.containsKey(uuid))
			this.load(uuid);
		this.homes.get(uuid).add(home);

		return home;
	}

	public void delete(UUID uuid, Home home) throws SQLException {
		if (this.homes.containsKey(uuid))
			this.homes.get(uuid).remove(home);

		this.getCore().getDatabase().deleteHome(home);
	}

	public List<Home> getHomesOfPlayer(UUID uuid) {
		if (this.homes.containsKey(uuid))
			return this.homes.get(uuid);
		return new ArrayList<Home>();
	}
}