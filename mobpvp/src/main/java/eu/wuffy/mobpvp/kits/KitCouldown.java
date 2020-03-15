package eu.wuffy.mobpvp.kits;

import java.util.HashMap;
import java.util.Map;

public class KitCouldown {

	private final Map<KitItems, Long> couldown = new HashMap<>();

	public void addCouldown(KitItems item, long couldown) {
		this.couldown.put(item, couldown);
	}

	public boolean hasCouldown(KitItems item) {
		long couldown = this.couldown.getOrDefault(item, -1L);

		if (couldown != -1 && couldown > System.currentTimeMillis()) {
			return true;
		}

		this.couldown.put(item, System.currentTimeMillis() + item.getCouldown());
		return false;
	}

	public void removeCouldown(KitItems item) {
		this.couldown.remove(item);
	}

	public void clearCouldown() {
		this.couldown.clear();
	}
}