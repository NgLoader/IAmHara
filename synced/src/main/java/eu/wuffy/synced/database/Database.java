package eu.wuffy.synced.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.stream.Stream;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.wuffy.synced.ICore;

public class Database {

	protected static final String DATABASE_PREFIX = "core_";

	protected static final DatabaseTable[] TABLE_CREATE = {
			new DatabaseTable(Database.DATABASE_PREFIX + "players", String.format(
					"CREATE TABLE `%splayers` ("
					+ "`id` INT AUTO_INCREMENT NOT NULL, "
					+ "`uuid` VARCHAR(36) NOT NULL, "
					+ "`username` VARCHAR(16), "
					+ "PRIMARY KEY (`id`)"
					+ ") DEFAULT CHARSET = utf8mb4", Database.DATABASE_PREFIX))
	};

	protected static final String PLAYER_SELECT_UUID_BY_USERNAME = String.format("SELECT uuid FROM %splayers WHERE username=? LIMIT 1", Database.DATABASE_PREFIX);
	protected static final String PLAYER_SELECT_USERNAME_BY_UUID = String.format("SELECT username FROM %splayers WHERE uuid=? LIMIT 1", Database.DATABASE_PREFIX);
	protected static final String PLAYER_SELECT_ID_BY_UUID = String.format("SELECT id FROM %splayers WHERE uuid=? LIMIT 1", Database.DATABASE_PREFIX);
	protected static final String PLAYER_UPDATE_USERNAME_FOR_UUID = String.format("UPDATE %splayers SET username=? WHERE uuid=?", Database.DATABASE_PREFIX);
	protected static final String PLAYER_INSERT_WITH_USERNAME = String.format("INSERT INTO %splayers (uuid, username) VALUES(?, ?)", Database.DATABASE_PREFIX);
	protected static final String PLAYER_INSERT = String.format("INSERT INTO `%splayers` (`uuid`) VALUES (?)", Database.DATABASE_PREFIX);

	protected final ICore core;

	protected final HikariDataSource dataSource;

	protected DatabaseTable[] defaultTables = Database.TABLE_CREATE;

	public Database(ICore core, HikariConfig config) {
		this.core = core;

		this.dataSource = new HikariDataSource(config);

		this.addDefaultTable(Database.TABLE_CREATE);
	}

	protected void addDefaultTable(DatabaseTable[] tables) {
		this.defaultTables = Stream.of(this.defaultTables, tables).flatMap(Stream::of).toArray(DatabaseTable[]::new);
	}

	public void createTables() throws SQLException {
		for (DatabaseTable table : this.defaultTables) {
			if (!this.existTable(table.tableName)) {
				try (Connection connection = this.getConnection(); Statement statement = connection.createStatement()) {
					statement.execute(table.createSql);
				}
			}
		}
	}

	public boolean existTable(String table) throws SQLException {
		try (Connection connection = this.getConnection()) {
			try (ResultSet resultSet = connection.getMetaData().getTables(null, null, "%", null)) {
				while (resultSet.next()) {
					if (resultSet.getString(3).equalsIgnoreCase(table)) {
						return true;
					}
				}
				return false;
			}
		}
	}

	public void closeConnection() {
		try {
			this.getConnection().commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.dataSource.close();
	}

	public Connection getConnection() throws SQLException {
		Connection connection = this.dataSource.getConnection();

		if (connection == null)
			throw new SQLException("Unable to get a connection from pool.");
		return connection;
	}

	public int getPlayerId(UUID uuid) throws SQLException {
		try (Connection connection = this.getConnection()) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(Database.PLAYER_SELECT_ID_BY_UUID)) {
				preparedStatement.setString(1, uuid.toString());

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						return resultSet.getInt("id");
					} else {
						try (PreparedStatement preparedStatement2 = connection.prepareStatement(Database.PLAYER_INSERT, new String[] { "id" })) {
							preparedStatement2.setString(1, uuid.toString());
							preparedStatement2.execute();

							try (ResultSet resultSet2 = preparedStatement2.getGeneratedKeys()) {
								if (resultSet2.next()) {
									return resultSet2.getInt("id");
								}
								throw new NullPointerException("Unable to get id for player " + uuid.toString());
							}
						}
					}
				}
			}
		}
	}

	public ICore getCore() {
		return this.core;
	}
}
