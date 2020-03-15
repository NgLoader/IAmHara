package eu.wuffy.disco.database;

import com.zaxxer.hikari.HikariConfig;

import eu.wuffy.core.database.CoreDatabase;
import eu.wuffy.synced.ICore;

public class DiscoDatabase extends CoreDatabase {

	public DiscoDatabase(ICore core, HikariConfig config) {
		super(core, config);
	}
}