package eu.wuffy.synced.database;

import eu.wuffy.synced.config.Config;

@Config(path = "Core", name = "database")
public class ConfigDatabase {
	public String dataSourceClassName = "org.mariadb.jdbc.MariaDbDataSource";

	public String serverName = "127.0.0.1";
	public int port = 3306;

	public String databaseName = "database";

	public String username = "username";
	public String password = "password";

	public long maxLifetime = 300000;
	public long connectionTimeout = 5000;
	public int minimumIdle = 10;
	public int maximumPoolSize = 10;
	public boolean autoCommit = true;
}
