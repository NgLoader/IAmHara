package eu.wuffy.core.database;

import eu.wuffy.synced.ICore;
import eu.wuffy.synced.database.ConfigDatabase;
import eu.wuffy.synced.database.Database;

public abstract class CoreDatabase extends Database {

	public CoreDatabase(ICore core, ConfigDatabase config) {
		super(core, config);
	}
}