package eu.wuffy.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.plugin.java.JavaPlugin;

import eu.wuffy.core.database.CoreDatabase;
import eu.wuffy.synced.ICore;
import eu.wuffy.synced.IHandler;

public abstract class Core<T extends CoreDatabase> extends JavaPlugin implements ICore {

	public static final List<Advancement> ADVANCEMENT_LIST;

	static {
		List<Advancement> advancements = new ArrayList<>();
		for (Iterator<Advancement> advancement = Bukkit.advancementIterator(); advancement.hasNext();) {
			advancements.add(advancement.next());
		}
		ADVANCEMENT_LIST = Collections.unmodifiableList(advancements);

		IHandler.setMessageAdapter((message) -> Bukkit.getConsoleSender().sendMessage(Core.PREFIX + message));
	}

	private T database;

	protected void setDatabase(T database) {
		this.database = database;
	}

	public T getDatabase() {
		return this.database;
	}
}