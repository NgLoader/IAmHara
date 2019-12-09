package eu.wuffy.mobpvp.database;

import com.zaxxer.hikari.HikariConfig;

import eu.wuffy.core.Core;
import eu.wuffy.core.database.CoreDatabase;

public class MobPvPDatabase extends CoreDatabase {

//	private static final String DATABASE_PREFIX = "mobpvp_";

	public MobPvPDatabase(Core<?> core, HikariConfig config) {
		super(core, config);
	}
}
