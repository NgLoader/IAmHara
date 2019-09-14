package eu.wuffy.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.plugin.java.JavaPlugin;

import eu.wuffy.core.database.CoreDatabase;
import eu.wuffy.synced.ICore;

public abstract class Core<T extends CoreDatabase> extends JavaPlugin implements ICore {

	public static final String PREFIX = "§8[§cCore§8] §7";

	public static final List<Advancement> ADVANCEMENT_LIST = new ArrayList<Advancement>();

	static {
		for (Iterator<Advancement> advancements = Bukkit.advancementIterator(); advancements.hasNext();) {
			Core.ADVANCEMENT_LIST.add(advancements.next());
		}
	}

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