package eu.wuffy.core.handler.notification;

import eu.wuffy.core.Core;

public abstract class NotificationMode<T extends Core<?>> {

	public abstract void onInit();
	public abstract void onEnable();
	public abstract void onDisable();

	public abstract boolean tick();
	public abstract void display(String text, long duration);

	protected final T core;

	public NotificationMode(T core) {
		this.core = core;
	}

	public T getCore() {
		return this.core;
	}
}