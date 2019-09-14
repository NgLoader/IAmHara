package eu.wuffy.core;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;

import eu.wuffy.core.database.CoreDatabase;

public abstract class IHandler<T extends Core<? extends CoreDatabase>> {

	private static final List<IHandler<?>> handlers = new LinkedList<IHandler<?>>();

	public static void destroy() {
		IHandler.handlers.stream().filter(handler -> handler.isEnabled()).forEach(IHandler::disable);
		IHandler.handlers.clear();
	}

	public static List<IHandler<?>> getHandlers() {
		return Collections.unmodifiableList(IHandler.handlers);
	}

	public abstract void onInit();
	public abstract void onEnable();
	public abstract void onDisable();

	private final T core;

	private boolean enabled = false;

	public IHandler(T core) {
		this.core = core;

		IHandler.handlers.add(this);
	}

	public void init() {
		try {
			this.onInit();
		} catch(Exception e) {
			e.printStackTrace();
		}
		Bukkit.getConsoleSender().sendMessage(Core.PREFIX + "Initialize handler §7\"§c" + this.getClass().getSimpleName() + "§7\"§8.");
	}

	public void enable() {
		try {
			this.onEnable();
			this.enabled = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		Bukkit.getConsoleSender().sendMessage(Core.PREFIX + "Enable handler §7\"§c" + this.getClass().getSimpleName() + "§7\"§8.");
	}

	public void disable() {
		try {
			this.enabled = false;
			this.onDisable();
		} catch(Exception e) {
			e.printStackTrace();
		}
		Bukkit.getConsoleSender().sendMessage(Core.PREFIX + "Disabled handler §7\"§c" + this.getClass().getSimpleName() + "§7\"§8.");
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public T getCore() {
		return this.core;
	}
}