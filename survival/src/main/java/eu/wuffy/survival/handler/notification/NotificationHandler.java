package eu.wuffy.survival.handler.notification;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.scheduler.BukkitTask;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.notification.modes.NotificationModeActionBar;
import eu.wuffy.survival.handler.notification.modes.NotificationModeBossBar;
import eu.wuffy.synced.IHandler;

public class NotificationHandler extends IHandler<Survival> {

	private final List<String> messages = new ArrayList<String>();
	private final List<NotificationMode> modes = new ArrayList<NotificationMode>();

	private BukkitTask scheduler;

	public NotificationHandler(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.modes.add(new NotificationModeBossBar(this.core));
		this.modes.add(new NotificationModeActionBar(this.core));

		this.messages.add("§cAbonniert §eIamHaRa §7mit §5TWITCH §bPRIME§8!");
		this.messages.add("§7Hier könnte ihre §aWerbung §7stehen.");
		this.messages.add("§6Pornhub Nutzer §7besuchten auch §6IamHaRa.de");

		this.modes.forEach(NotificationMode::onInit);
	}

	@Override
	public void onEnable() {
		this.modes.forEach(NotificationMode::onEnable);

		this.scheduler = this.core.getServer().getScheduler().runTaskTimerAsynchronously(this.core, new Runnable() {

			private NotificationMode mode = null;
			private long nextNotification = 0;

			@Override
			public void run() {
				if (this.mode != null) {
					if(!this.mode.tick()) {
						this.mode = null;
					}
					return;
				}

				if (this.nextNotification > System.currentTimeMillis()) {
					return;
				}
				this.nextNotification = System.currentTimeMillis() + 3600000; // 60min. => 3.600.000

				this.mode = modes.get(RandomUtils.nextInt(modes.size()));
				this.mode.display(messages.get(RandomUtils.nextInt(messages.size())), 15000);
			}
		}, 80, 2);
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