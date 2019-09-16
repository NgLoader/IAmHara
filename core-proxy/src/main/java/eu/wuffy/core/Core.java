package eu.wuffy.core;

import eu.wuffy.proxy.database.CoreDatabase;
import eu.wuffy.synced.ICore;
import eu.wuffy.synced.IHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

public abstract class Core<T extends CoreDatabase> extends Plugin implements ICore {

	static {
		IHandler.setMessageAdapter((message) -> ProxyServer.getInstance().getConsole().sendMessage(new TextComponent(Core.PREFIX + message)));
	}

	private T database;

	protected void setDatabase(T database) {
		this.database = database;
	}

	public T getDatabase() {
		return this.database;
	}
}