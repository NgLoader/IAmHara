package eu.wuffy.core.database;

import com.zaxxer.hikari.HikariConfig;

import eu.wuffy.synced.ICore;
import eu.wuffy.synced.database.Database;

public abstract class CoreDatabase extends Database {

	public CoreDatabase(ICore core, HikariConfig config) {
		super(core, config);
	}
}