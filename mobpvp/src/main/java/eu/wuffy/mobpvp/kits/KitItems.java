package eu.wuffy.mobpvp.kits;

import org.bukkit.Material;

public enum KitItems {

	BLAZE_FLAMETHROWER(Material.BLAZE_ROD, 5000);

	private Material material;
	private long couldown;

	private KitItems(Material material, int couldown) {
		this.material = material;
		this.couldown = couldown;
	}

	public Material getMaterial() {
		return material;
	}

	public long getCouldown() {
		return this.couldown;
	}
}