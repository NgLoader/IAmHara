package eu.wuffy.mobpvp.handler.damage;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.database.MobPvPDatabase;
import eu.wuffy.mobpvp.kits.KitType;

public class DamageStatus {

	private final Map<Player, Long> lastDamageTime = new HashMap<>();
	private final Map<Player, DamageStats> damageStats = new HashMap<>();

	private final MobPvPDatabase database;
	private final Player player;
	private final KitType kitType;

	public DamageStatus(MobPvPDatabase database, Player player, KitType kitType) {
		this.database = database;
		this.player = player;
		this.kitType = kitType;
	}

	public void addDamage(Player damager, KitType kitType, double damage) {
		long lastDamage = System.currentTimeMillis();
		this.lastDamageTime.put(damager, lastDamage);

		DamageStats stats = this.damageStats.get(damager);
		if (stats == null || stats.getLastDamage() + 15000 < lastDamage) {
			stats = new DamageStats();
			this.damageStats.put(damager, stats);
		}

		stats.addDamage(kitType, damage, lastDamage);
	}

	public void removeDamager(Player damager) {
		this.lastDamageTime.remove(damager);
		this.damageStats.remove(player);
	}

	public void finish() {
		Optional<Entry<Player, Long>> lastDamager = this.lastDamageTime.entrySet().stream().sorted(Comparator.comparingLong(entry -> entry.getValue())).findFirst();
		long currentlyTime = System.currentTimeMillis() - 15000;

		if (lastDamager.isPresent() && lastDamager.get().getValue() > currentlyTime) {
			Bukkit.broadcastMessage(String.format("%s§c%s §7wurde von §4%s §cgetötet§8.", MobPvP.PREFIX, player.getDisplayName(), lastDamager.get().getKey().getDisplayName()));

			for (Entry<Player, Long> entry : this.lastDamageTime.entrySet()) {
				if (entry.getValue() > currentlyTime) {
					Player damager = entry.getKey();
					DamageStats damageStats = this.damageStats.get(damager);

					if (damageStats != null) {
						for (Entry<KitType, Double> damageEntry : damageStats.getDamageByKits().entrySet()) {
							database.addDamageMade(damager.getUniqueId(), damageEntry.getKey(), this.kitType, damageEntry.getValue());
							database.addDamageTake(this.player.getUniqueId(), this.kitType, damageEntry.getKey(), damageEntry.getValue());
						}
					}
				}
			}
		} else {
			Bukkit.broadcastMessage(String.format("%s§c%s §7ist §4gestorben§8.", MobPvP.PREFIX, player.getDisplayName()));
		}

		database.addDeath(this.player.getUniqueId(), this.kitType);

		this.lastDamageTime.clear();
		this.damageStats.clear();
	}
}