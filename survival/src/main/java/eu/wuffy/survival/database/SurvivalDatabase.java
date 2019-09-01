package eu.wuffy.survival.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.zaxxer.hikari.HikariConfig;

import eu.wuffy.core.database.CoreDatabase;
import eu.wuffy.survival.Survival;
import eu.wuffy.survival.home.Home;
import eu.wuffy.survival.warp.Warp;
import eu.wuffy.survival.warp.WarpAlias;
import eu.wuffy.synced.database.Database;
import eu.wuffy.synced.database.DatabaseTable;

public class SurvivalDatabase extends CoreDatabase {

	private static final String DATABASE_PREFIX = "survival_";

	private static final DatabaseTable[] TABLES = {
			new DatabaseTable(SurvivalDatabase.DATABASE_PREFIX + "warps", String.format(
					"CREATE TABLE `%swarps` ("
					+ "`warp_id` INT AUTO_INCREMENT NOT NULL, "
					+ "`name` VARCHAR(255) 			NOT NULL, "
					+ "`description` VARCHAR(255) 	NOT NULL, "
					+ "`permission` VARCHAR(255) 	NOT NULL, "
					+ "`world` VARCHAR(255) 		NOT NULL, "
					+ "`x` DOUBLE	 				NOT NULL, "
					+ "`y` DOUBLE 					NOT NULL, "
					+ "`z` DOUBLE 					NOT NULL, "
					+ "`yaw` FLOAT 					NOT NULL, "
					+ "`pitch` FLOAT 				NOT NULL, "
					+ "PRIMARY KEY (`warp_id`)"
					+ ") DEFAULT CHARSET = utf8mb4", SurvivalDatabase.DATABASE_PREFIX)),
			new DatabaseTable(SurvivalDatabase.DATABASE_PREFIX + "warp_aliases", String.format(
					"CREATE TABLE `%swarp_aliases` ("
					+ "`alias_id` INT AUTO_INCREMENT 	NOT NULL, "
					+ "`warp_id` INT 					NOT NULL, "
					+ "`alias` VARCHAR(255) 			NOT NULL, "
					+ "PRIMARY KEY (`alias_id`),"
					+ "FOREIGN KEY (`warp_id`) REFERENCES %1$swarps(`warp_id`) ON DELETE CASCADE"
					+ ") DEFAULT CHARSET = utf8mb4", SurvivalDatabase.DATABASE_PREFIX)),

			new DatabaseTable(SurvivalDatabase.DATABASE_PREFIX + "homes", String.format(
					"CREATE TABLE `%shomes` ("
					+ "`home_id` INT AUTO_INCREMENT 	NOT NULL, "
					+ "`player_id` INT 					NOT NULL, "
					+ "`name` VARCHAR(255) 				NOT NULL, "
					+ "`description` VARCHAR(255) 		NOT NULL, "
					+ "`world` VARCHAR(255) 			NOT NULL, "
					+ "`x` DOUBLE	 					NOT NULL, "
					+ "`y` DOUBLE 						NOT NULL, "
					+ "`z` DOUBLE 						NOT NULL, "
					+ "`yaw` FLOAT 						NOT NULL, "
					+ "`pitch` FLOAT 					NOT NULL, "
					+ "PRIMARY KEY (`home_id`),"
					+ "FOREIGN KEY (`player_id`) REFERENCES %2$splayers(`id`) ON DELETE CASCADE"
					+ ") DEFAULT CHARSET = utf8mb4", SurvivalDatabase.DATABASE_PREFIX, Database.DATABASE_PREFIX))
	};

	private static final String WARP_LIST = String.format("SELECT * FROM %swarps", SurvivalDatabase.DATABASE_PREFIX);
	private static final String WARP_INSERT = String.format("INSERT INTO %swarps (name, description, permission, world, x, y, z, yaw, pitch) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", SurvivalDatabase.DATABASE_PREFIX);
	private static final String WARP_DELETE = String.format("DELETE FROM %swarps WHERE warp_id=?", SurvivalDatabase.DATABASE_PREFIX);

	private static final String WARP_ALIAS_LIST = String.format("SELECT * FROM %swarp_aliases WHERE warp_id=? LIMIT 50", SurvivalDatabase.DATABASE_PREFIX);
	private static final String WARP_ALIAS_INSERT = String.format("INSERT INTO %swarp_aliases (warp_id, alias) VALUES(?, ?)", SurvivalDatabase.DATABASE_PREFIX);
	private static final String WARP_ALIAS_DELETE = String.format("DELETE FROM %swarp_aliases WHERE alias_id=?", SurvivalDatabase.DATABASE_PREFIX);

	private static final String HOME_LIST_BY_UUID = String.format("SELECT * FROM %shomes WHERE player_id IN (SELECT id FROM %splayers WHERE uuid=?) LIMIT 50", SurvivalDatabase.DATABASE_PREFIX, Database.DATABASE_PREFIX);
	private static final String HOME_INSERT = String.format("INSERT INTO %shomes (player_id, name, description, world, x, y, z, yaw, pitch) VALUES((SELECT id FROM %splayers WHERE uuid=?), ?, ?, ?, ?, ?, ?, ?, ?)", SurvivalDatabase.DATABASE_PREFIX, Database.DATABASE_PREFIX);
	private static final String HOME_DELETE = String.format("DELETE FROM %shomes WHERE home_id=?", SurvivalDatabase.DATABASE_PREFIX);

	public SurvivalDatabase(Survival core, HikariConfig config) {
		super(core, config);

		this.addDefaultTable(SurvivalDatabase.TABLES);
	}

	/*
	 * Warp
	 */
	public List<Warp> loadWarps() {
		List<Warp> warps = new ArrayList<Warp>();

		try (Connection connection = this.getConnection()) {
			try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(SurvivalDatabase.WARP_LIST)) {
				while (resultSet.next()) {
					int warpId = resultSet.getInt("warp_id");
					warps.add(new Warp(
							warpId,
							resultSet.getString("name"),
							resultSet.getString("description"),
							resultSet.getString("permission"),
							resultSet.getString("world"),
							resultSet.getDouble("x"),
							resultSet.getDouble("y"),
							resultSet.getDouble("z"),
							resultSet.getFloat("yaw"),
							resultSet.getFloat("pitch"),
							this.getWarpAliases(warpId)));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return warps;
	}

	public Warp addWarp(String name, String description, String permission, String world, double x, double y, double z, float yaw, float pitch) {
		try (Connection connection = this.getConnection()) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.WARP_INSERT, new String[] { "warp_id" })) {
				preparedStatement.setString(1, name);
				preparedStatement.setString(2, description);
				preparedStatement.setString(3, permission);
				preparedStatement.setString(4, world);
				preparedStatement.setDouble(5, x);
				preparedStatement.setDouble(6, y);
				preparedStatement.setDouble(7, z);
				preparedStatement.setFloat(8, yaw);
				preparedStatement.setFloat(9, pitch);
				preparedStatement.execute();

				try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
					if (resultSet.next()) {
						int warpId = resultSet.getInt("warp_id");
						return new Warp(
								warpId,
								name,
								description,
								permission,
								world,
								x,
								y,
								z,
								yaw,
								pitch,
								this.getWarpAliases(warpId));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteWarp(Warp warp) {
		try (Connection connection = this.getConnection()) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.WARP_DELETE)) {
				preparedStatement.setInt(1, warp.warpId);
				preparedStatement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<WarpAlias> getWarpAliases(int warpId) {
		List<WarpAlias> alias = new ArrayList<WarpAlias>();

		try (Connection connection = this.getConnection()) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.WARP_ALIAS_LIST)) {
				preparedStatement.setInt(1, warpId);

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while(resultSet.next()) {
						alias.add(new WarpAlias(resultSet.getInt("alias_id"), resultSet.getInt("warp_id"), resultSet.getString("alias")));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return alias;
	}

	public WarpAlias addWarpAlias(Warp warp, String alias) {
		try (Connection connection = this.getConnection()) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.WARP_ALIAS_INSERT, new String[] { "alias_id" })) {
				preparedStatement.setInt(1, warp.warpId);
				preparedStatement.setString(2, alias);
				preparedStatement.execute();

				try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
					if (resultSet.next()) {
						return new WarpAlias(
								resultSet.getInt("alias_id"),
								warp.warpId,
								alias);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteWarpAlias(WarpAlias alias) {
		try (Connection connection = this.getConnection()) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.WARP_ALIAS_DELETE)) {
				preparedStatement.setInt(1, alias.aliasId);
				preparedStatement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Home
	 */
	public List<Home> loadHomes(UUID uuid) throws SQLException {
		List<Home> homes = new ArrayList<Home>();
	
		try (Connection connection = this.getConnection()) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(HOME_LIST_BY_UUID)) {
				preparedStatement.setString(1, uuid.toString());

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						homes.add(new Home(
								resultSet.getInt("home_id"),
								resultSet.getString("name"),
								resultSet.getString("description"),
								resultSet.getString("world"),
								resultSet.getDouble("x"),
								resultSet.getDouble("y"),
								resultSet.getDouble("z"),
								resultSet.getFloat("yaw"),
								resultSet.getFloat("pitch")));
					}
				}
			}
		}

		return homes;
	}

	public Home createHome(UUID uuid, String name, String description, String world, double x, double y, double z, float yaw, float pitch) throws SQLException {
		try (Connection connection = this.getConnection()) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(HOME_INSERT, new String[] { "home_id" })) {
				preparedStatement.setString(1, uuid.toString());
				preparedStatement.setString(2, name);
				preparedStatement.setString(3, description);
				preparedStatement.setString(4, world);
				preparedStatement.setDouble(5, x);
				preparedStatement.setDouble(6, y);
				preparedStatement.setDouble(7, z);
				preparedStatement.setFloat(8, yaw);
				preparedStatement.setFloat(9, pitch);
				preparedStatement.execute();

				try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
					if (resultSet.next()) {
						return new Home(resultSet.getInt("home_id"), name, description, world, x, y, z, yaw, pitch);
					}
				}

				throw new NullPointerException("Error by creating new home for " + uuid.toString());
			}
		}
	}

	public void deleteHome(Home home) throws SQLException {
		try (Connection connection = this.getConnection()) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.HOME_DELETE)) {
				preparedStatement.setInt(1, home.homeId);
				preparedStatement.execute();
			}
		}
	}
}
