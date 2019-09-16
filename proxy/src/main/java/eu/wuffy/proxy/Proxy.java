package eu.wuffy.proxy;

import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;

import eu.wuffy.proxy.database.ProxyDatabase;
import eu.wuffy.proxy.event.ServerSwitchEventListener;
import eu.wuffy.synced.ICore;
import eu.wuffy.synced.IHandler;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.event.EventBus;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class Proxy extends Plugin implements ICore {

	private final ProxyDatabase database;

	private LuckPermsApi luckPermsApi;

	public Proxy() {
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

		this.database = new ProxyDatabase(this, databaseConfig);

		IHandler.setMessageAdapter((message) -> ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(Proxy.PREFIX + message)));
	}

	@Override
	public void onLoad() {
		IHandler.getHandlers().forEach(IHandler::init);
	}

	@Override
	public void onEnable() {
		this.luckPermsApi = LuckPerms.getApi();

		try {
			this.getDatabase().createTables();
		} catch(SQLException e) {
			e.printStackTrace();

			this.getProxy().getConsole().sendMessage(new TextComponent(Proxy.PREFIX + "§4Error by connecting to database§8!"));
			return;
		}

		IHandler.getHandlers().forEach(IHandler::enable);

		this.registerListener();
		this.registerCommands();

		this.getProxy().getConsole().sendMessage(new TextComponent(Proxy.PREFIX + "§2Enabled§8!"));
	}

	@Override
	public void onDisable() {
		IHandler.destroy();
		this.getDatabase().closeConnection();

		this.getProxy().getScheduler().cancel(this);

		this.getProxy().getConsole().sendMessage(new TextComponent(Proxy.PREFIX + "§4Disabled§8!"));
	}

	public void registerListener() {
		PluginManager pluginManager = this.getProxy().getPluginManager();
		EventBus eventBus = this.luckPermsApi.getEventBus();

		pluginManager.registerListener(this, new ServerSwitchEventListener());
	}

	public void registerCommands() {
		PluginManager pluginManager = this.getProxy().getPluginManager();
	}

	public ProxyDatabase getDatabase() {
		return this.database;
	}

	public LuckPermsApi getLuckPermsApi() {
		return this.luckPermsApi;
	}
}