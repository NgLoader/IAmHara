package eu.wuffy.proxy.database;

import eu.wuffy.synced.ICore;
import eu.wuffy.synced.database.ConfigDatabase;
import eu.wuffy.synced.database.Database;

public class ProxyDatabase extends Database {

	public ProxyDatabase(ICore core, ConfigDatabase config) {
		super(core, config);
	}
}