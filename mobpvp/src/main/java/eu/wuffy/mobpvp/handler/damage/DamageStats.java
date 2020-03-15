package eu.wuffy.mobpvp.handler.damage;

import java.util.HashMap;
import java.util.Map;

import eu.wuffy.mobpvp.kits.KitType;

public class DamageStats {

	private final Map<KitType, Double> damageByKit = new HashMap<>();

	private long lastDamage;

	public void addDamage(KitType kitType, double damage, long lastDamage) {
		damage += this.damageByKit.getOrDefault(kitType, 0d);
		this.lastDamage = lastDamage;

		this.damageByKit.put(kitType, damage);
	}

	public Map<KitType, Double> getDamageByKits() {
		return this.damageByKit;
	}

	public long getLastDamage() {
		return this.lastDamage;
	}
}