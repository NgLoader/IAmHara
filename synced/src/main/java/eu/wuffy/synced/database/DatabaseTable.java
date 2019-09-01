package eu.wuffy.synced.database;

public class DatabaseTable {

	public String tableName;
	public String createSql;

	public DatabaseTable(String tableName, String createSql) {
		this.tableName = tableName;
		this.createSql = createSql;
	}
}