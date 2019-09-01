package eu.wuffy.survival.warp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import eu.wuffy.survival.Survival;

public class WarpHandler {

	private Survival core;

	private Map<String, Warp> warpsByAlias = new HashMap<String, Warp>();
	private List<Warp> warps = new LinkedList<Warp>();

	public WarpHandler(Survival core) {
		this.core = core;
	}

	public void init() {
		this.warps.clear();

		for(Warp warp : this.core.getDatabase().loadWarps()) {
			this.warps.add(warp);
			this.warpsByAlias.put(warp.name.toLowerCase(), warp);

			warp.aliases.forEach(alias -> this.warpsByAlias.put(alias.alias.toLowerCase(), warp));
		}
	}

	public Warp get(String name) {
		if (this.warpsByAlias.containsKey(name))
			return this.warpsByAlias.get(name);

		for (Warp warp : this.warpsByAlias.values())
			if (warp.aliases.stream().anyMatch(alias -> alias.alias.equalsIgnoreCase(name)))
				return warp;
		return null;
	}

	public boolean exist(String name) {
		if (this.warpsByAlias.containsKey(name))
			return true;

		for (Warp warp : this.warpsByAlias.values())
			if (warp.aliases.stream().anyMatch(alias -> alias.alias.equalsIgnoreCase(name)))
				return true;
		return false;
	}

	public Warp create(String name, String description, String permission, Location location, List<String> aliases) {
		if (this.exist(name.toLowerCase()))
			return null;

		Warp warp = this.core.getDatabase().addWarp(name, description, permission, location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

		this.warps.add(warp);
		this.warpsByAlias.put(warp.name.toLowerCase(), warp);
		warp.aliases.forEach(alias -> this.warpsByAlias.put(alias.alias.toLowerCase(), warp));

		return warp;
	}

	public void delete(Warp warp) {
		this.warps.remove(warp);
		this.warpsByAlias.remove(warp.name.toLowerCase());
		warp.aliases.forEach(alias -> this.warpsByAlias.remove(alias.alias.toLowerCase()));

		this.core.getDatabase().deleteWarp(warp);
	}

	public WarpAlias createAlias(Warp warp, String alias) {
		if (this.exist(alias))
			return null;

		WarpAlias warpAlias = this.core.getDatabase().addWarpAlias(warp, alias);

		warp.aliases.add(warpAlias);
		this.warpsByAlias.put(alias.toLowerCase(), warp);

		return warpAlias;
	}

	public void deleteAlias(WarpAlias alias) {
		this.warpsByAlias.remove(alias.alias);

		this.core.getDatabase().deleteWarpAlias(alias);
	}

	public List<Warp> getWarps() {
		return this.warps;
	}

	public Survival getCore() {
		return this.core;
	}
}