package eu.wuffy.mobpvp.handler;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.synced.IHandler;

public class DamageHandler extends IHandler<MobPvP> {

	private final Map<Player, DamageStatus> playerDamage = new HashMap<Player, DamageStatus>();

	public DamageHandler(MobPvP core) {
		super(core);
	}

	public DamageStatus getDamageState(Player player) {
		DamageStatus damageStatus = this.playerDamage.get(player);

		if (damageStatus == null) {
			damageStatus = new DamageStatus(player);
			this.playerDamage.put(player, damageStatus);
		}

		return damageStatus;
	}

	public DamageStatus remove(Player player) {
		return this.playerDamage.remove(player);
	}

	public class DamageStatus {

		private final Map<Player, Long> lastDamageTime = new HashMap<Player, Long>();
		private final Map<Player, Double> damageMade = new HashMap<Player, Double>();

		private final Player player;

		public DamageStatus(Player player) {
			this.player = player;
		}

		public void addDamage(Player damager, double damage) {
			this.lastDamageTime.put(damager, System.currentTimeMillis());
			this.damageMade.put(damager, damage);
		}

		public void removeDamager(Player damager) {
			this.lastDamageTime.remove(damager);
			this.damageMade.remove(player);
		}

		public void finish() {
			Optional<Entry<Player, Long>> lastDamager = this.lastDamageTime.entrySet().stream().sorted(Comparator.comparingLong(entry -> entry.getValue())).findFirst();
			long currentlyTime = System.currentTimeMillis() - 15000;

			if (lastDamager.isPresent() && lastDamager.get().getValue() > currentlyTime) {
				Bukkit.broadcastMessage(String.format("%s §c%s %7wurde von §4%s §cgetötet§8.", MobPvP.PREFIX, player.getCustomName(), lastDamager.get().getKey().getDisplayName()));
			} else {
				Bukkit.broadcastMessage(String.format("%s §c%s §7ist §4gestorben§8.", MobPvP.PREFIX, player.getCustomName()));
			}

			this.lastDamageTime.clear();
			this.damageMade.clear();
		}
	}
}