package eu.wuffy.survival.handler.notification;

import eu.wuffy.survival.Survival;

public abstract class NotificationMode {

	public abstract void onInit();
	public abstract void onEnable();
	public abstract void onDisable();

	public abstract boolean tick();
	public abstract void display(String text, long duration);

	protected final Survival core;

	public NotificationMode(Survival core) {
		this.core = core;
	}

	public Survival getCore() {
		return this.core;
	}
}