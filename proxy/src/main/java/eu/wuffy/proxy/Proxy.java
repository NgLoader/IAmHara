package eu.wuffy.proxy;

import java.sql.SQLException;

import eu.wuffy.proxy.command.CommandAlert;
import eu.wuffy.proxy.command.CommandAlertRaw;
import eu.wuffy.proxy.command.CommandFind;
import eu.wuffy.proxy.command.CommandGlobalList;
import eu.wuffy.proxy.command.CommandLobby;
import eu.wuffy.proxy.command.CommandMsg;
import eu.wuffy.proxy.command.CommandSend;
import eu.wuffy.proxy.command.CommandServer;
import eu.wuffy.proxy.database.ProxyDatabase;
import eu.wuffy.proxy.event.ServerSwitchEventListener;
import eu.wuffy.synced.ICore;
import eu.wuffy.synced.IHandler;
import eu.wuffy.synced.config.ConfigService;
import eu.wuffy.synced.database.ConfigDatabase;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class Proxy extends Plugin implements ICore {

	public static final String PREFIX = "§8[§2Zanrux§8] §7";
	private final ProxyDatabase database;

	public Proxy() {
		this.database = new ProxyDatabase(this, ConfigService.getConfig(ConfigDatabase.class));

		IHandler.setMessageAdapter((message) -> ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(Proxy.PREFIX + message)));
	}

	@Override
	public void onLoad() {
		IHandler.getHandlers().forEach(IHandler::init);
	}

	@Override
	public void onEnable() {
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
		pluginManager.registerListener(this, new ServerSwitchEventListener());
	}

	public void registerCommands() {
		PluginManager pluginManager = this.getProxy().getPluginManager();
		pluginManager.registerCommand(this, new CommandServer());
		pluginManager.registerCommand(this, new CommandSend());
		pluginManager.registerCommand(this, new CommandGlobalList());
		pluginManager.registerCommand(this, new CommandFind());
		pluginManager.registerCommand(this, new CommandLobby());
		pluginManager.registerCommand(this, new CommandAlertRaw());
		pluginManager.registerCommand(this, new CommandAlert());
		pluginManager.registerCommand(this, new CommandMsg());
	}

	public ProxyDatabase getDatabase() {
		return this.database;
	}
}