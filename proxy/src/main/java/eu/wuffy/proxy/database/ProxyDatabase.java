package eu.wuffy.proxy.database;

import com.zaxxer.hikari.HikariConfig;

import eu.wuffy.synced.ICore;
import eu.wuffy.synced.database.Database;

public class ProxyDatabase extends Database {

	public ProxyDatabase(ICore core, HikariConfig config) {
		super(core, config);
	}
}