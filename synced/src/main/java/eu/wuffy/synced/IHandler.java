package eu.wuffy.synced;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class IHandler<T extends ICore> {

	private static final List<IHandler<?>> handlers = new LinkedList<IHandler<?>>();
	private static Consumer<String> messageAdapter;

	public static List<IHandler<?>> getHandlers() {
		return Collections.unmodifiableList(IHandler.handlers);
	}

	public static void setMessageAdapter(Consumer<String> messageAdapter) {
		IHandler.messageAdapter = messageAdapter;
	}

	public static void destroy() {
		IHandler.handlers.stream().filter(handler -> handler.isEnabled()).forEach(IHandler::disable);
		IHandler.handlers.clear();
	}

	public void onInit() { }
	public void onEnable() { }
	public void onDisable() { }

	protected final T core;

	private boolean enabled = false;

	public IHandler(T core) {
		this.core = core;

		IHandler.handlers.add(this);
	}

	public void init() {
		IHandler.messageAdapter.accept("Initialize handler §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
		try {
			this.onInit();
		} catch(Exception e) {
			e.printStackTrace();
			IHandler.messageAdapter.accept("§cError by initialize handler §7\"§c" + this.getClass().getSimpleName() + "§7\"§8.");
			return;
		}
		IHandler.messageAdapter.accept("Initialized handler §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
	}

	public void enable() {
		IHandler.messageAdapter.accept("Enable handler §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
		try {
			this.onEnable();
			this.enabled = true;
		} catch(Exception e) {
			e.printStackTrace();
			IHandler.messageAdapter.accept("§cError by enable handler §7\"§c" + this.getClass().getSimpleName() + "§7\"§8.");
			return;
		}
		IHandler.messageAdapter.accept("Enabled handler §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
	}

	public void disable() {
		IHandler.messageAdapter.accept("Disable handler §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
		try {
			this.enabled = false;
			this.onDisable();
		} catch(Exception e) {
			e.printStackTrace();
			IHandler.messageAdapter.accept("§cError by disable handler §7\"§c" + this.getClass().getSimpleName() + "§7\"§8.");
			return;
		}
		IHandler.messageAdapter.accept("Disabled handler §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public T getCore() {
		return this.core;
	}
}