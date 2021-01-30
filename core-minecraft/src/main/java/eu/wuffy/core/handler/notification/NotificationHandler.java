package eu.wuffy.core.handler.notification;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.scheduler.BukkitTask;

import eu.wuffy.core.Core;
import eu.wuffy.core.handler.notification.mode.NotificationModeActionBar;
import eu.wuffy.core.handler.notification.mode.NotificationModeBossBar;
import eu.wuffy.synced.IHandler;

public class NotificationHandler<T extends Core<?>> extends IHandler<T> implements Runnable {

	protected final List<String> messages = new ArrayList<String>();
	protected final List<NotificationMode<T>> modes = new ArrayList<NotificationMode<T>>();

	protected int delay = 1000 * 60 * 30;

	protected NotificationMode<T> currentMode;
	protected long nextNotification = 0;

	private BukkitTask scheduler;

	public NotificationHandler(T core) {
		super(core);
	}

	@Override
	public void run() {
		if (this.currentMode != null && !this.messages.isEmpty()) {
			if(!this.currentMode.tick()) {
				this.currentMode = null;
			}
			return;
		}

		if (this.nextNotification > System.currentTimeMillis()) {
			return;
		}
		this.nextNotification = System.currentTimeMillis() + this.delay;

		this.currentMode = modes.get(RandomUtils.nextInt(modes.size()));
		this.currentMode.display(messages.get(RandomUtils.nextInt(messages.size())), 15000);
	}

	@Override
	public void onInit() {
		this.modes.add(new NotificationModeBossBar<T>(this.core));
		this.modes.add(new NotificationModeActionBar<T>(this.core));
		this.modes.forEach(NotificationMode::onInit);
	}

	@Override
	public void onEnable() {
		this.modes.forEach(NotificationMode::onEnable);
		this.scheduler = this.core.getServer().getScheduler().runTaskTimerAsynchronously(this.core, this, 80, 2);
	}

	@Override
	public void onDisable() {
		if (this.scheduler != null) {
			this.scheduler.cancel();
			this.scheduler = null;
		}

		this.modes.forEach(NotificationMode::onDisable);
	}
}