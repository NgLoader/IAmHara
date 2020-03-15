package eu.wuffy.disco;

import org.bukkit.Bukkit;

import com.zaxxer.hikari.HikariConfig;

import eu.wuffy.core.Core;
import eu.wuffy.core.database.CoreDatabase;
import eu.wuffy.core.handler.ChatHandler;
import eu.wuffy.core.scoreboard.ScoreboardHandler;
import eu.wuffy.disco.command.CommandBuild;
import eu.wuffy.disco.command.CommandFly;
import eu.wuffy.disco.command.CommandGameMode;
import eu.wuffy.disco.command.CommandPing;
import eu.wuffy.disco.database.DiscoDatabase;
import eu.wuffy.disco.listener.BuildHandler;
import eu.wuffy.disco.listener.DisallowHandler;
import eu.wuffy.disco.listener.InventoryHandler;
import eu.wuffy.disco.listener.PlayerHandler;
import eu.wuffy.disco.listener.ProdigyNightClubHandler;
import eu.wuffy.disco.listener.TeleportHandler;
import eu.wuffy.synced.IHandler;

public class Disco extends Core<CoreDatabase> {

	public static final String PREFIX = "§8[§6Disco§8] ";

	private final boolean whitelist;

	private final ScoreboardHandler scoreboardHandler;
	private final ChatHandler chatHandler;
	private final DisallowHandler disallowHandler;
	private final InventoryHandler inventoryHandler;
	private final BuildHandler buildHandler;
	private final PlayerHandler playerHandler;
	private final TeleportHandler teleportHandler;
	private final ProdigyNightClubHandler nightClubHandler;

	public Disco() {
		this.whitelist = Bukkit.hasWhitelist();
		Bukkit.setWhitelist(true);

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

		this.setDatabase(new DiscoDatabase(this, databaseConfig));

		this.scoreboardHandler = new ScoreboardHandler(this);
		this.chatHandler = new ChatHandler(this);
		this.buildHandler = new BuildHandler(this);
		this.inventoryHandler = new InventoryHandler(this);
		this.disallowHandler = new DisallowHandler(this);
		this.playerHandler = new PlayerHandler(this);
		this.teleportHandler = new TeleportHandler(this);
		this.nightClubHandler = new ProdigyNightClubHandler(this);
	}

	@Override
	public void onLoad() {
		IHandler.getHandlers().forEach(IHandler::init);
	}

	@Override
	public void onEnable() {
		IHandler.getHandlers().forEach(IHandler::enable);

		this.getCommand("buildmode").setExecutor(new CommandBuild(this));
		this.getCommand("gamemode").setExecutor(new CommandGameMode());
		this.getCommand("fly").setExecutor(new CommandFly());
		this.getCommand("ping").setExecutor(new CommandPing());

		Bukkit.getConsoleSender().sendMessage(Disco.PREFIX + "§2Enabled§8!");

		if (!this.whitelist) {
			Bukkit.setWhitelist(false);
		}
	}

	@Override
	public void onDisable() {
		IHandler.getHandlers().forEach(IHandler::disable);

		IHandler.destroy();
		Bukkit.getScheduler().cancelTasks(this);

		this.getDatabase().closeConnection();

		Bukkit.getConsoleSender().sendMessage(Disco.PREFIX + "§4Disabled§8!");
	}

	public ScoreboardHandler getScoreboardHandler() {
		return this.scoreboardHandler;
	}

	public ChatHandler getChatHandler() {
		return this.chatHandler;
	}

	public DisallowHandler getDisallowHandler() {
		return this.disallowHandler;
	}

	public BuildHandler getBuildHandler() {
		return this.buildHandler;
	}

	public InventoryHandler getInventoryHandler() {
		return this.inventoryHandler;
	}

	public PlayerHandler getPlayerHandler() {
		return this.playerHandler;
	}

	public TeleportHandler getTeleportHandler() {
		return this.teleportHandler;
	}

	public ProdigyNightClubHandler getNightClubHandler() {
		return this.nightClubHandler;
	}
}