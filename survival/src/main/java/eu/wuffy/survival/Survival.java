package eu.wuffy.survival;

import java.sql.SQLException;

import org.bukkit.Bukkit;

import com.zaxxer.hikari.HikariConfig;

import eu.wuffy.core.Core;
import eu.wuffy.core.handler.ChatHandler;
import eu.wuffy.core.help.HelpSystem;
import eu.wuffy.core.inventory.InventorySystem;
import eu.wuffy.core.scoreboard.ScoreboardHandler;
import eu.wuffy.survival.command.CommandClaimTool;
import eu.wuffy.survival.command.CommandEnderchest;
import eu.wuffy.survival.command.CommandPing;
import eu.wuffy.survival.command.CommandSpawn;
import eu.wuffy.survival.command.CommandTreeFeller;
import eu.wuffy.survival.command.admin.CommandAdminTool;
import eu.wuffy.survival.command.admin.CommandFly;
import eu.wuffy.survival.command.admin.CommandGameMode;
import eu.wuffy.survival.command.admin.CommandInvsee;
import eu.wuffy.survival.command.admin.CommandSay;
import eu.wuffy.survival.command.admin.CommandSurvival;
import eu.wuffy.survival.command.admin.CommandVanish;
import eu.wuffy.survival.command.economy.CommandEconomyBalance;
import eu.wuffy.survival.command.help.CommandHelp;
import eu.wuffy.survival.command.home.CommandHome;
import eu.wuffy.survival.command.home.CommandHomeCreate;
import eu.wuffy.survival.command.home.CommandHomeDelete;
import eu.wuffy.survival.command.home.CommandHomeList;
import eu.wuffy.survival.command.tp.CommandTp;
import eu.wuffy.survival.command.tp.CommandTpHere;
import eu.wuffy.survival.command.tpa.CommandTpa;
import eu.wuffy.survival.command.tpa.CommandTpaccept;
import eu.wuffy.survival.command.tpa.CommandTpadeny;
import eu.wuffy.survival.command.tpa.CommandTpahere;
import eu.wuffy.survival.command.tpa.CommandTpalist;
import eu.wuffy.survival.command.warp.CommandWarp;
import eu.wuffy.survival.command.warp.CommandWarpCreate;
import eu.wuffy.survival.command.warp.CommandWarpCreateAlias;
import eu.wuffy.survival.command.warp.CommandWarpDelete;
import eu.wuffy.survival.command.warp.CommandWarpDeleteAlias;
import eu.wuffy.survival.command.warp.CommandWarpList;
import eu.wuffy.survival.database.SurvivalDatabase;
import eu.wuffy.survival.handler.ChairHandler;
import eu.wuffy.survival.handler.SleepHandler;
import eu.wuffy.survival.handler.SurvivalHologramHandler;
import eu.wuffy.survival.handler.SurvivalNotificationHandler;
import eu.wuffy.survival.handler.TreeFellerHandler;
import eu.wuffy.survival.handler.VanishHandler;
import eu.wuffy.survival.handler.event.EventHandler;
import eu.wuffy.survival.handler.home.HomeHandler;
import eu.wuffy.survival.handler.tpa.TPAHandler;
import eu.wuffy.survival.handler.warp.WarpHandler;
import eu.wuffy.synced.IHandler;

public class Survival extends Core<SurvivalDatabase> {

	public static final String PREFIX = "§8[§2Survival§8] §7";

	private final boolean whitelist;

	private final InventorySystem inventorySystem;
	private final HelpSystem helpSystem;
	private final ScoreboardHandler scoreboardHandler;
	private final ChatHandler chatHandler;
	private final WarpHandler warpHandler;
	private final HomeHandler homeHandler;
	private final VanishHandler vanishHandler;
//	private final InventoryHandler inventoryHandler;
	private final TreeFellerHandler treeFellerHandler;
	private final SurvivalNotificationHandler notificationHandler;
//	private final StorageHandler storageHandler;
	private final EventHandler eventHandler;
	private final SurvivalHologramHandler hologramHandler;
	private final TPAHandler tpaHandler;
	private final ChairHandler chairHandler;
	private final SleepHandler sleepHandler;

	public Survival() {
		this.whitelist = Bukkit.hasWhitelist();
		Bukkit.setWhitelist(true);

		HikariConfig databaseConfig = new HikariConfig();
		databaseConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
		databaseConfig.addDataSourceProperty("serverName", "173.249.17.9");
		databaseConfig.addDataSourceProperty("port", 3306);
		databaseConfig.addDataSourceProperty("databaseName", "minecraft_zanrux");
		databaseConfig.setUsername("minecraft_zanrux");
		databaseConfig.setPassword("AZjjdO23IrsIVThw");

		databaseConfig.setMaxLifetime(300000);
		databaseConfig.setConnectionTimeout(5000);
		databaseConfig.setMinimumIdle(10);
		databaseConfig.setMaximumPoolSize(10);
		databaseConfig.setAutoCommit(true);

		this.setDatabase(new SurvivalDatabase(this, databaseConfig));

		this.inventorySystem = new InventorySystem(this);
		this.helpSystem = new HelpSystem(this, PREFIX);
		this.scoreboardHandler = new ScoreboardHandler(this);
		this.chatHandler = new ChatHandler(this);
		this.warpHandler = new WarpHandler(this);
		this.homeHandler = new HomeHandler(this);
		this.vanishHandler = new VanishHandler(this);
//		this.inventoryHandler = new InventoryHandler(this);
		this.treeFellerHandler = new TreeFellerHandler(this);
		this.notificationHandler = new SurvivalNotificationHandler(this);
//		this.storageHandler = new StorageHandler(this);
		this.eventHandler = new EventHandler(this);
		this.hologramHandler = new SurvivalHologramHandler(this);
		this.tpaHandler = new TPAHandler(this);
		this.chairHandler = new ChairHandler(this, PREFIX);
		this.sleepHandler = new SleepHandler(this, PREFIX);
	}

	@Override
	public void onLoad() {
		IHandler.getHandlers().forEach(IHandler::init);
	}

	@Override
	public void onEnable() {
		try {
			this.getDatabase().createTables();
		} catch (SQLException e) {
			e.printStackTrace();

			Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§4Error by connecting to database§8!");
			return;
		}

		IHandler.getHandlers().forEach(IHandler::enable);

		this.registerCommands();

		Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§2Enabled§8!");

		if (!this.whitelist) {
			Bukkit.setWhitelist(false);
		}
	}

	@Override
	public void onDisable() {
		Bukkit.setWhitelist(true);

		IHandler.destroy();
		this.getDatabase().closeConnection();

		Bukkit.getScheduler().cancelTasks(this);

		Bukkit.getConsoleSender().sendMessage(Survival.PREFIX + "§4Disabled§8!");
	}

	private void registerCommands() {
		getCommand("ping").setExecutor(new CommandPing());
		getCommand("spawn").setExecutor(new CommandSpawn());
		getCommand("treefeller").setExecutor(new CommandTreeFeller(this));
		getCommand("claimtool").setExecutor(new CommandClaimTool());
		getCommand("enderchest").setExecutor(new CommandEnderchest());

		getCommand("survival").setExecutor(new CommandSurvival(this));
		getCommand("gamemode").setExecutor(new CommandGameMode());
		getCommand("admintool").setExecutor(new CommandAdminTool());
		getCommand("invsee").setExecutor(new CommandInvsee());
		getCommand("vanish").setExecutor(new CommandVanish(this));
		getCommand("say").setExecutor(new CommandSay());
		getCommand("fly").setExecutor(new CommandFly());

		getCommand("home").setExecutor(new CommandHome(this));
		getCommand("homelist").setExecutor(new CommandHomeList(this));
		getCommand("homecreate").setExecutor(new CommandHomeCreate(this));
		getCommand("homedelete").setExecutor(new CommandHomeDelete(this));

		getCommand("warp").setExecutor(new CommandWarp(this));
		getCommand("warpcreate").setExecutor(new CommandWarpCreate(this));
		getCommand("warpdelete").setExecutor(new CommandWarpDelete(this));
		getCommand("warplist").setExecutor(new CommandWarpList(this));
		getCommand("warpcreatealias").setExecutor(new CommandWarpCreateAlias(this));
		getCommand("warpdeletealias").setExecutor(new CommandWarpDeleteAlias(this));

		getCommand("help").setExecutor(new CommandHelp(this));

		getCommand("balance").setExecutor(new CommandEconomyBalance(this));

		getCommand("tp").setExecutor(new CommandTp());
		getCommand("tphere").setExecutor(new CommandTpHere());
		getCommand("tpa").setExecutor(new CommandTpa(this));
		getCommand("tpaccept").setExecutor(new CommandTpaccept(this));
		getCommand("tpadeny").setExecutor(new CommandTpadeny(this));
		getCommand("tpahere").setExecutor(new CommandTpahere(this));
		getCommand("tpalist").setExecutor(new CommandTpalist(this));
	}

	public InventorySystem getInventorySystem() {
		return this.inventorySystem;
	}

	public HelpSystem getHelpSystem() {
		return this.helpSystem;
	}

	public ScoreboardHandler getScoreboardHandler() {
		return this.scoreboardHandler;
	}

	public ChatHandler getChatHandler() {
		return this.chatHandler;
	}

	public WarpHandler getWarpHandler() {
		return this.warpHandler;
	}

	public HomeHandler getHomeHandler() {
		return this.homeHandler;
	}

	public VanishHandler getVanishHandler() {
		return this.vanishHandler;
	}

//	public InventoryHandler getInventoryHandler() {
//		return this.inventoryHandler;
//	}

	public TreeFellerHandler getTreeFellerHandler() {
		return this.treeFellerHandler;
	}

	public SurvivalNotificationHandler getNotificationHandler() {
		return this.notificationHandler;
	}

//	public StorageHandler getStorageHandler() {
//		return this.storageHandler;
//	}

	public EventHandler getEventHandler() {
		return this.eventHandler;
	}

	public SurvivalHologramHandler getHologramHandler() {
		return this.hologramHandler;
	}

	public TPAHandler getTpaHandler() {
		return this.tpaHandler;
	}

	public ChairHandler getChairHandler() {
		return this.chairHandler;
	}

	public SleepHandler getSleepHandler() {
		return this.sleepHandler;
	}
}