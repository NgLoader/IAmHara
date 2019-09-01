package eu.wuffy.core;

import org.bukkit.plugin.java.JavaPlugin;

import eu.wuffy.core.database.CoreDatabase;
import eu.wuffy.synced.ICore;

public abstract class Core<T extends CoreDatabase> extends JavaPlugin implements ICore {

	@Override
	public abstract void onEnable();

	@Override
	public abstract void onDisable();

	private T database;

	protected void setDatabase(T database) {
		this.database = database;
	}

	public T getDatabase() {
		return this.database;
	}
}