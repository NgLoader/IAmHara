package eu.wuffy.mobpvp;

import java.sql.SQLException;

import org.bukkit.Bukkit;

import com.zaxxer.hikari.HikariConfig;

import eu.wuffy.core.Core;
import eu.wuffy.core.database.CoreDatabase;
import eu.wuffy.mobpvp.database.MobPvPDatabase;
import eu.wuffy.mobpvp.handler.LocationHandler;
import eu.wuffy.mobpvp.handler.event.EventHandler;
import eu.wuffy.mobpvp.kits.KitHandler;
import eu.wuffy.synced.IHandler;

public class MobPvP extends Core<CoreDatabase> {

	public static final String PREFIX = "§8[§cMobPvP§8] ";

	private final LocationHandler locationHandler;
	private final KitHandler kitHandler;
	private final EventHandler eventHandler;

	public MobPvP() {
		HikariConfig databaseConfig = new HikariConfig();
		databaseConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
		databaseConfig.addDataSourceProperty("serverName", "173.249.17.9");
		databaseConfig.addDataSourceProperty("port", 3306);
		databaseConfig.addDataSourceProperty("databaseName", "minecraft");
		databaseConfig.setUsername("minecraft");
		databaseConfig.setPassword("GjVFNgg7zzVZuGKVLGue2sTM8K6GZchxdwuDk4Xkcb2ymFGrsVnhn3RLgzwfcgYB6cuCPC72x9ehxHUpjLccLNm5dSwUTuekUMxnNsVkcKA3SJaC5qyGpQ3n6w8S9PSD");

		databaseConfig.setMaxLifetime(300000);
		databaseConfig.setConnectionTimeout(5000);
		databaseConfig.setMinimumIdle(10);
		databaseConfig.setMaximumPoolSize(10);
		databaseConfig.setAutoCommit(true);

		this.setDatabase(new MobPvPDatabase(this, databaseConfig));

		this.locationHandler = new LocationHandler(this);
		this.kitHandler = new KitHandler(this);
		this.eventHandler = new EventHandler(this);
	}

	@Override
	public void onLoad() {
		Bukkit.setWhitelist(true);

		IHandler.getHandlers().forEach(IHandler::init);
	}

	@Override
	public void onEnable() {
		try {
			this.getDatabase().createTables();
		} catch (SQLException e) {
			e.printStackTrace();

			Bukkit.getConsoleSender().sendMessage(MobPvP.PREFIX + "§4Error by connecting to database§8!");
			return;
		}

		IHandler.getHandlers().forEach(IHandler::enable);

		Bukkit.getConsoleSender().sendMessage(MobPvP.PREFIX + "§2Enabled§8!");
		Bukkit.setWhitelist(false);
	}

	@Override
	public void onDisable() {
		Bukkit.setWhitelist(true);

		IHandler.destroy();
		this.getDatabase().closeConnection();

		Bukkit.getScheduler().cancelTasks(this);

		Bukkit.getConsoleSender().sendMessage(MobPvP.PREFIX + "§4Disabled§8!");
	}

	public LocationHandler getLocationHandler() {
		return this.locationHandler;
	}

	public KitHandler getKitHandler() {
		return this.kitHandler;
	}

	public EventHandler getEventHandler() {
		return this.eventHandler;
	}
}