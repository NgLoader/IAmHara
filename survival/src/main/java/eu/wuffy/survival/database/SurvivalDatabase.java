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
import eu.wuffy.survival.handler.home.Home;
import eu.wuffy.survival.handler.warp.Warp;
import eu.wuffy.survival.handler.warp.WarpAlias;
import eu.wuffy.synced.database.Database;
import eu.wuffy.synced.database.DatabaseTable;

public class SurvivalDatabase extends CoreDatabase {

	private static final String DATABASE_PREFIX = "survival_";

	private static final DatabaseTable[] TABLES_WARPS = {
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
					+ ") DEFAULT CHARSET = utf8mb4", SurvivalDatabase.DATABASE_PREFIX))
	};

	private static final DatabaseTable[] TABLES_HOMES = {
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

//	private static final DatabaseTable[] TABLES_PLAYERINFO = {
//			new DatabaseTable(SurvivalDatabase.DATABASE_PREFIX + "playerdata", String.format(
//					"CREATE TABLE `%splayerdata` ("
//					+ "`playerdata_id` INT AUTO_INCREMENT 	NOT NULL, "
//					+ "`player_id` INT 						NOT NULL, "
//					+ "`gamemode` INT 						NOT NULL, "
//					+ "`exp` FLOAT	 						NOT NULL, "
//					+ "`level` INT 							NOT NULL, "
//					+ "`totalExperience` INT 				NOT NULL, "
//					+ "`saturation` FLOAT 					NOT NULL, "
//					+ "`foodLevel` INT 						NOT NULL, "
//					+ "`healthScale` DOUBLE 				NOT NULL, "
//					+ "`healthScaled` BOOLEAN 				NOT NULL, "
//					+ "`flySpeed` FLOAT 					NOT NULL, "
//					+ "`walkSpeed` FLOAT 					NOT NULL, "
//					+ "PRIMARY KEY (`playerdata_id`),"
//					+ "FOREIGN KEY (`player_id`) REFERENCES %2$splayers(`id`) ON DELETE CASCADE"
//					+ ") DEFAULT CHARSET = utf8mb4", SurvivalDatabase.DATABASE_PREFIX, Database.DATABASE_PREFIX)),
//			new DatabaseTable(SurvivalDatabase.DATABASE_PREFIX + "playerdata_inventory", String.format(
//					"CREATE TABLE `%splayerdata_inventory` ("
//					+ "`playerdata_id` INT 							NOT NULL, "
//					+ "`content` VARCHAR(2560) 						NOT NULL, "
//					+ "`armorContent` VARCHAR(2560) 				NOT NULL, "
//					+ "`storageContent` VARCHAR(2560)				NOT NULL, "
//					+ "`extraContent` VARCHAR(2560)					NOT NULL, "
//					+ "`enderchestContent` VARCHAR(2560) 			NOT NULL, "
//					+ "`enderchestStorageContent` VARCHAR(2560) 	NOT NULL, "
//					+ "FOREIGN KEY (`playerdata_id`) REFERENCES %1$splayerdata(`playerdata_id`) ON DELETE CASCADE"
//					+ ") DEFAULT CHARSET = utf8mb4", SurvivalDatabase.DATABASE_PREFIX)),
//			new DatabaseTable(SurvivalDatabase.DATABASE_PREFIX + "playerdata_statistic", String.format(
//					"CREATE TABLE `%splayerdata_statistic` ("
//					+ "`playerdata_id` INT 				NOT NULL, "
//					+ "`name` VARCHAR(255) 				NOT NULL, "
//					+ "`type` VARCHAR(255) 				NOT NULL, "
//					+ "`material` VARCHAR(255), 				  "
//					+ "`value` INT 						NOT NULL, "
//					+ "FOREIGN KEY (`playerdata_id`) REFERENCES %1$splayerdata(`playerdata_id`) ON DELETE CASCADE"
//					+ ") DEFAULT CHARSET = utf8mb4", SurvivalDatabase.DATABASE_PREFIX)),
//			new DatabaseTable(SurvivalDatabase.DATABASE_PREFIX + "playerdata_advancement", String.format(
//					"CREATE TABLE `%splayerdata_advancement` ("
//					+ "`playerdata_id` INT 				NOT NULL, "
//					+ "`advancement` VARCHAR(255) 		NOT NULL, "
//					+ "`criteria` VARCHAR(255) 			NOT NULL, "
//					+ "`date` DATE						NOT NULL, "
//					+ "FOREIGN KEY (`playerdata_id`) REFERENCES %1$splayerdata(`playerdata_id`) ON DELETE CASCADE"
//					+ ") DEFAULT CHARSET = utf8mb4", SurvivalDatabase.DATABASE_PREFIX))
//	};

//	private static final DatabaseTable[] TABLES_ECONOMY = {
//			new DatabaseTable(SurvivalDatabase.DATABASE_PREFIX + "economy_players", String.format(
//					"CREATE TABLE `%seconomy_players` ("
//					+ "`economy_id` VARCHAR(255) 		NOT NULL, "
//					+ "`balance` DOUBLE					NOT NULL, "
//					+ "PRIMARY KEY (`economy_id`)"
//					+ ") DEFAULT CHARSET = utf8mb4", SurvivalDatabase.DATABASE_PREFIX, Database.DATABASE_PREFIX)),
//			new DatabaseTable(SurvivalDatabase.DATABASE_PREFIX + "economy_banks", String.format(
//					"CREATE TABLE `%seconomy_banks` ("
//					+ "`bank_id` VARCHAR(255)			NOT NULL, "
//					+ "`owner` VARCHAR(255)				NOT NULL, "
//					+ "`balance` DOUBLE					NOT NULL, "
//					+ "PRIMARY KEY (`bank_id`)"
//					+ ") DEFAULT CHARSET = utf8mb4", SurvivalDatabase.DATABASE_PREFIX, Database.DATABASE_PREFIX)),
//			new DatabaseTable(SurvivalDatabase.DATABASE_PREFIX + "economy_players_banks", String.format(
//					"CREATE TABLE `%seconomy_players_banks` ("
//					+ "`bank_id` VARCHAR(255)			NOT NULL, "
//					+ "`player` VARCHAR(255) 	 		NOT NULL, "
//					+ "PRIMARY KEY (`bank_id`, `player`),"
//					+ "FOREIGN KEY (`bank_id`) REFERENCES %2$seconomy_banks(`bank_id`) ON DELETE CASCADE"
//					+ ") DEFAULT CHARSET = utf8mb4", SurvivalDatabase.DATABASE_PREFIX, SurvivalDatabase.DATABASE_PREFIX))
//	};

	private static final String WARP_LIST = String.format("SELECT * FROM %swarps", SurvivalDatabase.DATABASE_PREFIX);
	private static final String WARP_INSERT = String.format("INSERT INTO %swarps (name, description, permission, world, x, y, z, yaw, pitch) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", SurvivalDatabase.DATABASE_PREFIX);
	private static final String WARP_DELETE = String.format("DELETE FROM %swarps WHERE warp_id=?", SurvivalDatabase.DATABASE_PREFIX);

	private static final String WARP_ALIAS_LIST = String.format("SELECT * FROM %swarp_aliases WHERE warp_id=? LIMIT 50", SurvivalDatabase.DATABASE_PREFIX);
	private static final String WARP_ALIAS_INSERT = String.format("INSERT INTO %swarp_aliases (warp_id, alias) VALUES(?, ?)", SurvivalDatabase.DATABASE_PREFIX);
	private static final String WARP_ALIAS_DELETE = String.format("DELETE FROM %swarp_aliases WHERE alias_id=?", SurvivalDatabase.DATABASE_PREFIX);

	private static final String HOME_LIST_BY_UUID = String.format("SELECT * FROM %shomes WHERE player_id IN (SELECT id FROM %splayers WHERE uuid=?) LIMIT 50", SurvivalDatabase.DATABASE_PREFIX, Database.DATABASE_PREFIX);
	private static final String HOME_INSERT = String.format("INSERT INTO %shomes (player_id, name, description, world, x, y, z, yaw, pitch) VALUES((SELECT id FROM %splayers WHERE uuid=?), ?, ?, ?, ?, ?, ?, ?, ?)", SurvivalDatabase.DATABASE_PREFIX, Database.DATABASE_PREFIX);
	private static final String HOME_DELETE = String.format("DELETE FROM %shomes WHERE home_id=?", SurvivalDatabase.DATABASE_PREFIX);

//	private static final String PLAYERDATA_GET_BY_UUID = String.format("SELECT * FROM %splayerdata WHERE player_id IN (SELECT id FROM %splayers WHERE uuid=?)", SurvivalDatabase.DATABASE_PREFIX, Database.DATABASE_PREFIX);
//	private static final String INVENTORY_INSERT = String.format("INSERT INTO %splayerdata (player_id, gamemode, content, armorContent, storageContent, extraContent, enderchestContent) VALUES((SELECT id FROM %splayers WHERE uuid=?), ?, ?, ?, ?, ?, ?)", SurvivalDatabase.DATABASE_PREFIX, Database.DATABASE_PREFIX);
//	private static final String INVENTORY_UPDATE = String.format("UPDATE %splayerdata SET gamemode = ?, content = ?, armorContent = ?, storageContent = ?, extraContent = ?, enderchestContent = ?", SurvivalDatabase.DATABASE_PREFIX);
//	private static final String INVENTORY_DELETE = String.format("DELETE FROM %splayerdata WHERE playerdata_id=?", SurvivalDatabase.DATABASE_PREFIX);

	private static final String ECONOMY_PLAYER_EXIST = String.format("SELECT * FROM %seconomy_players WHERE economy_id=? LIMIT 1", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_PLAYER_CREATE = String.format("INSERT INTO %seconomy_players(economy_id, balance) VALUES(?, ?)", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_PLAYER_DELETE = String.format("DELETE FROM %seconomy_players WHERE economy_id=?", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_PLAYER_GET_MONEY = String.format("SELECT balance FROM %seconomy_players WHERE economy_id=? LIMIT 1", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_PLAYER_SET_MONEY = String.format("UPDATE %seconomy_players SET balance=? WHERE economy_id=?", SurvivalDatabase.DATABASE_PREFIX);

	private static final String ECONOMY_BANK_EXIST = String.format("SELECT * FROM %seconomy_banks WHERE bank_id=? LIMIT 1", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_BANK_CREATE = String.format("INSERT INTO %seconomy_banks(bank_id, owner, balance) VALUES(?, ?, ?)", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_BANK_DELETE = String.format("DELETE FROM %seconomy_banks WHERE bank_id=?", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_BANK_GET_ALL = String.format("SELECT * FROM %seconomy_banks LIMIT 1000", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_BANK_GET_OWNER = String.format("SELECT owner FROM %seconomy_banks WHERE bank_id=? LIMIT 1", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_BANK_SET_OWNER = String.format("UPDATE %seconomy_banks SET owner=? WHERE bank_id=?", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_BANK_GET_MONEY = String.format("SELECT balance FROM %seconomy_banks WHERE bank_id=? LIMIT 1", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_BANK_SET_MONEY = String.format("UPDATE %seconomy_banks SET balance=? WHERE bank_id=?", SurvivalDatabase.DATABASE_PREFIX);

	private static final String ECONOMY_IS_PLAYER_IN_BANK = String.format("SELECT * FROM %seconomy_players_banks WHERE bank_id=?, player=? LIMIT 1", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_ADD_PLAYER_TO_BANK = String.format("INSERT INTO %seconomy_players_banks(bank_id, player) VALUES(?, ?)", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_DELETE_PLAYER_FROM_BANK = String.format("DELETE FROM %seconomy_players_banks WHERE bank_id=?, player=?", SurvivalDatabase.DATABASE_PREFIX);
	private static final String ECONOMY_GET_BANKS_BY_PLAYER = String.format("SELECT * FROM %seconomy_players_banks WHERE player=?", SurvivalDatabase.DATABASE_PREFIX);

	public SurvivalDatabase(Survival core, HikariConfig config) {
		super(core, config);

		this.addDefaultTable(SurvivalDatabase.TABLES_WARPS);
		this.addDefaultTable(SurvivalDatabase.TABLES_HOMES);
//		this.addDefaultTable(SurvivalDatabase.TABLES_PLAYERINFO);
//		this.addDefaultTable(SurvivalDatabase.TABLES_ECONOMY);
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

	/*
	public DatabaseInventory getInventory(UUID uuid, int gamemode, ItemStack[] content, ItemStack[] armorContent, ItemStack[] storageContent, ItemStack[] extraContent, ItemStack[] enderchestContent) throws SQLException {
		try (Connection connection = this.getConnection()) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.INVENTORY_GET_BY_UUID)) {
				preparedStatement.setString(1, uuid.toString());

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (resultSet.next()) {
						return new DatabaseInventory(
								resultSet.getInt("inventory_id"),
								resultSet.getInt("gamemode"),
								resultSet.getString("content"),
								resultSet.getString("armorContent"),
								resultSet.getString("storageContent"),
								resultSet.getString("extraContent"),
								resultSet.getString("enderchestContent"));
					} else {
						try (PreparedStatement preparedStatement2 = connection.prepareStatement(SurvivalDatabase.INVENTORY_INSERT, new String[] { "inventory_id" })) {
							preparedStatement2.setString(1, uuid.toString());
							preparedStatement2.setInt(2, gamemode);
							preparedStatement2.setString(3, ItemFactory.itemStackArrayToBase64(content));
							preparedStatement2.setString(4, ItemFactory.itemStackArrayToBase64(armorContent));
							preparedStatement2.setString(5, ItemFactory.itemStackArrayToBase64(storageContent));
							preparedStatement2.setString(6, ItemFactory.itemStackArrayToBase64(extraContent));
							preparedStatement2.setString(7, ItemFactory.itemStackArrayToBase64(enderchestContent));
							preparedStatement2.execute();

							try (ResultSet resultSet2 = preparedStatement2.getGeneratedKeys()) {
								if (resultSet2.next()) {
									return new DatabaseInventory(
											resultSet2.getInt("inventory_id"),
											gamemode,
											content,
											armorContent,
											storageContent,
											extraContent,
											enderchestContent);
								}
								throw new NullPointerException("Unable to insert inventory for player " + uuid.toString());
							}
						}
					}
				}
			}
		}
	}

	public void updateInventory(UUID uuid, int gamemode, ItemStack[] content, ItemStack[] armorContent, ItemStack[] storageContent, ItemStack[] extraContent, ItemStack[] enderchestContent) throws SQLException {

		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.INVENTORY_UPDATE)) {
			preparedStatement.setString(1, uuid.toString());
			preparedStatement.setInt(2, gamemode);
			preparedStatement.setString(3, ItemFactory.itemStackArrayToBase64(content));
			preparedStatement.setString(4, ItemFactory.itemStackArrayToBase64(armorContent));
			preparedStatement.setString(5, ItemFactory.itemStackArrayToBase64(storageContent));
			preparedStatement.setString(6, ItemFactory.itemStackArrayToBase64(extraContent));
			preparedStatement.setString(7, ItemFactory.itemStackArrayToBase64(enderchestContent));
			preparedStatement.execute();

			try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
				if (resultSet.next()) {
					// Player was inserted!
					return;
				}
				throw new NullPointerException("Unable to insert inventory for player " + uuid.toString());
			}
		}
	}

	public void deleteInventory(DatabaseInventory inventory) throws SQLException {
		try (Connection connection = this.getConnection()) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.INVENTORY_DELETE)) {
				preparedStatement.setInt(1, inventory.inventory_id);
				preparedStatement.execute();
			}
		}
	}
	*/

	public boolean economyPlayerExist(String name) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_PLAYER_EXIST)) {
			preparedStatement.setString(1, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	public void economyPlayerCreate(String name, double balance) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_PLAYER_CREATE)) {
			preparedStatement.setString(1, name);
			preparedStatement.setDouble(2, balance);
			preparedStatement.execute();
		}
	}

	public void economyPlayerDelete(String name) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_PLAYER_DELETE)) {
			preparedStatement.setString(1, name);
			preparedStatement.execute();
		}
	}

	public double economyPlayerMoneyGet(String name) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_PLAYER_GET_MONEY)) {
			preparedStatement.setString(1, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getDouble("balance");
				}
			}
		}

		return 0;
	}

	public void economyPlayerMoneySet(String name, double balance) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_PLAYER_SET_MONEY)) {
			preparedStatement.setDouble(1, balance);
			preparedStatement.setString(2, name);
			preparedStatement.execute();
		}
	}

	public boolean economyBankExist(String name) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_BANK_EXIST)) {
			preparedStatement.setString(1, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	public void economyBankCreate(String name, String owner, double balance) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_BANK_CREATE)) {
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, owner);
			preparedStatement.setDouble(3, balance);
			preparedStatement.execute();
		}
	}

	public void economyBankDelete(String name) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_BANK_DELETE)) {
			preparedStatement.setString(1, name);
			preparedStatement.execute();
		}
	}

	public double economyBankMoneyGet(String name) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_BANK_GET_MONEY)) {
			preparedStatement.setString(1, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getDouble("balance");
				}
			}
		}

		return 0;
	}

	public void economyBankMoneySet(String name, double balance) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_BANK_SET_MONEY)) {
			preparedStatement.setDouble(1, balance);
			preparedStatement.setString(2, name);
			preparedStatement.execute();
		}
	}

	public String economyBankOwnerGet(String name) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_BANK_GET_OWNER)) {
			preparedStatement.setString(1, name);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString("owner");
				}
			}
		}

		return null;
	}

	public void economyBankOwnerSet(String name, String owner) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_BANK_SET_OWNER)) {
			preparedStatement.setString(1, owner);
			preparedStatement.setString(2, name);
			preparedStatement.execute();
		}
	}

	public boolean economyBankIsPlayerIn(String name, String player) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_IS_PLAYER_IN_BANK)) {
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, player);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				return resultSet.next();
			}
		}
	}

	public void economyBankAddPlayer(String name, String player) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_ADD_PLAYER_TO_BANK)) {
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, player);
			preparedStatement.execute();
		}
	}

	public void economyBankRemovePlayer(String name, String player) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_DELETE_PLAYER_FROM_BANK)) {
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, player);
			preparedStatement.execute();
		}
	}

	public List<String> economyBanksByPlayer(String player) throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_GET_BANKS_BY_PLAYER)) {
			preparedStatement.setString(1, player);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				List<String> banks = new ArrayList<String>();

				while (resultSet.next()) {
					banks.add("bank_id");
				}

				return banks;
			}
		}
	}

	public List<String> economyBanks() throws SQLException {
		try (Connection connection = this.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SurvivalDatabase.ECONOMY_BANK_GET_ALL)) {
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				List<String> banks = new ArrayList<String>();

				while (resultSet.next()) {
					banks.add("bank_id");
				}

				return banks;
			}
		}
	}
}
