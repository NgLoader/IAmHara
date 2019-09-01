package eu.wuffy.survival;

import java.sql.SQLException;

import org.bukkit.Bukkit;

import com.zaxxer.hikari.HikariConfig;

import eu.wuffy.core.Core;
import eu.wuffy.survival.command.CommandPing;
import eu.wuffy.survival.command.CommandSpawn;
import eu.wuffy.survival.command.admin.CommandAdminTool;
import eu.wuffy.survival.command.admin.CommandFly;
import eu.wuffy.survival.command.admin.CommandGameMode;
import eu.wuffy.survival.command.admin.CommandInvsee;
import eu.wuffy.survival.command.admin.CommandSay;
import eu.wuffy.survival.command.home.CommandHome;
import eu.wuffy.survival.command.home.CommandHomeCreate;
import eu.wuffy.survival.command.home.CommandHomeDelete;
import eu.wuffy.survival.command.home.CommandHomeList;
import eu.wuffy.survival.command.warp.CommandWarp;
import eu.wuffy.survival.command.warp.CommandWarpCreate;
import eu.wuffy.survival.command.warp.CommandWarpCreateAlias;
import eu.wuffy.survival.command.warp.CommandWarpDelete;
import eu.wuffy.survival.command.warp.CommandWarpDeleteAlias;
import eu.wuffy.survival.command.warp.CommandWarpList;
import eu.wuffy.survival.database.SurvivalDatabase;
import eu.wuffy.survival.event.AsyncPlayerChatEventListener;
import eu.wuffy.survival.event.PlayerJoinEventListener;
import eu.wuffy.survival.event.PlayerQuitEventListener;
import eu.wuffy.survival.handler.ScoreboardHandler;
import eu.wuffy.survival.home.HomeHandler;
import eu.wuffy.survival.warp.WarpHandler;

public class Survival extends Core<SurvivalDatabase> {

	public static final String PREFIX = "§8[§2Survival§8] ";

	private ScoreboardHandler scoreboardHandler;
	private WarpHandler warpHandler;
	private HomeHandler homeHandler;

	public Survival() {
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

		this.setDatabase(new SurvivalDatabase(this, databaseConfig));

		this.scoreboardHandler = new ScoreboardHandler(this);
		this.warpHandler = new WarpHandler(this);
		this.homeHandler = new HomeHandler(this);
	}

	@Override
	public void onEnable() {
		Bukkit.setWhitelist(true);

		try {
			this.getDatabase().createTables();
		} catch (SQLException e) {
			e.printStackTrace();

			Bukkit.getConsoleSender().sendMessage("&8[&aSurival&8] &4Error by connecting to database&8!");
			return;
		}

		this.getScoreboardHandler().init();
		this.getWarpHandler().init();
		this.getHomeHandler().init();

		this.getDatabase().loadWarps();

		this.registerListener();
		this.registerCommands();

		Bukkit.getConsoleSender().sendMessage("&8[$aSurival&8] &2Enabled");
		Bukkit.setWhitelist(false);
	}

	@Override
	public void onDisable() {
		this.getDatabase().closeConnection();

		Bukkit.getConsoleSender().sendMessage("&8[$aSurival&8] &4Disabled");
	}

	private void registerListener() {
		Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitEventListener(this), this);
	}

	private void registerCommands() {
		getCommand("warp").setExecutor(new CommandWarp(this));
		getCommand("warpcreate").setExecutor(new CommandWarpCreate(this));
		getCommand("warpdelete").setExecutor(new CommandWarpDelete(this));
		getCommand("warplist").setExecutor(new CommandWarpList(this));
		getCommand("warpcreatealias").setExecutor(new CommandWarpCreateAlias(this));
		getCommand("warpdeletealias").setExecutor(new CommandWarpDeleteAlias(this));
		getCommand("home").setExecutor(new CommandHome(this));
		getCommand("homelist").setExecutor(new CommandHomeList(this));
		getCommand("homecreate").setExecutor(new CommandHomeCreate(this));
		getCommand("homedelete").setExecutor(new CommandHomeDelete(this));
		getCommand("gamemode").setExecutor(new CommandGameMode());
		getCommand("fly").setExecutor(new CommandFly());
		getCommand("admintool").setExecutor(new CommandAdminTool());
		getCommand("invsee").setExecutor(new CommandInvsee());
		getCommand("say").setExecutor(new CommandSay());
		getCommand("ping").setExecutor(new CommandPing());
		getCommand("spawn").setExecutor(new CommandSpawn());
	}

	public ScoreboardHandler getScoreboardHandler() {
		return this.scoreboardHandler;
	}

	public WarpHandler getWarpHandler() {
		return this.warpHandler;
	}

	public HomeHandler getHomeHandler() {
		return this.homeHandler;
	}
}