package eu.wuffy.survival.handler.event;

import org.bukkit.event.Listener;

import eu.wuffy.survival.Survival;

public class EventListener implements Listener {

	public void onInit() { }
	public void onEnable() { }
	public void onDisable() { }

	protected final Survival core;

	private boolean enabled = false;

	public EventListener(Survival core) {
		this.core = core;
	}

	public void init() {
		this.core.getServer().getConsoleSender().sendMessage(Survival.PREFIX + "§7Initialize event §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
		try {
			this.onInit();
		} catch(Exception e) {
			e.printStackTrace();
			this.core.getServer().getConsoleSender().sendMessage(Survival.PREFIX + "§cError by initialize event §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
			return;
		}
		this.core.getServer().getConsoleSender().sendMessage(Survival.PREFIX + "§7Initialized event §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
	}

	public void enable() {
		this.core.getServer().getConsoleSender().sendMessage(Survival.PREFIX + "§7Enable event §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
		try {
			this.onEnable();
			this.enabled = true;

			this.core.getServer().getPluginManager().registerEvents(this, this.getCore());
		} catch(Exception e) {
			e.printStackTrace();
			this.core.getServer().getConsoleSender().sendMessage(Survival.PREFIX + "§cError by enable event §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
			return;
		}
		this.core.getServer().getConsoleSender().sendMessage(Survival.PREFIX + "§7Enabled event §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
	}

	public void disable() {
		this.core.getServer().getConsoleSender().sendMessage(Survival.PREFIX + "§7Disable event §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
		try {
			this.enabled = false;
			this.onDisable();
		} catch(Exception e) {
			e.printStackTrace();
			this.core.getServer().getConsoleSender().sendMessage(Survival.PREFIX + "§cError by disabled event §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
			return;
		}
		this.core.getServer().getConsoleSender().sendMessage(Survival.PREFIX + "§7Disabled event §7\"§2" + this.getClass().getSimpleName() + "§7\"§8.");
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public Survival getCore() {
		return this.core;
	}
}