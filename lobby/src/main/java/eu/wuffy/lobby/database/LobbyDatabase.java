package eu.wuffy.lobby.database;

import com.zaxxer.hikari.HikariConfig;

import eu.wuffy.core.database.CoreDatabase;
import eu.wuffy.synced.ICore;

public class LobbyDatabase extends CoreDatabase {

	public LobbyDatabase(ICore core, HikariConfig config) {
		super(core, config);
	}
}
