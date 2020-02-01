package eu.wuffy.mobpvp.database;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.zaxxer.hikari.HikariConfig;

import eu.wuffy.core.Core;
import eu.wuffy.core.database.CoreDatabase;
import eu.wuffy.mobpvp.kits.KitType;

public class MobPvPDatabase extends CoreDatabase {

//	private static final String DATABASE_PREFIX = "mobpvp_";

	public MobPvPDatabase(Core<?> core, HikariConfig config) {
		super(core, config);
	}

	public List<Map.Entry<UUID, Integer>> getTop10(KitType type) {
		return null;
	}

	public int getKills(UUID uuid, KitType type) {
		return 0;
	}

	public int getDeaths(UUID uuid, KitType type) {
		return 0;
	}

	public double getDamageMade(UUID uuid, KitType type) {
		return 0;
	}

	public void addKill(UUID uuid, KitType type) {
	}

	public void addDeath(UUID uuid, KitType type) {
	}

	public void addDamageMade(UUID uuid, KitType type, double damage) {
	}
}
