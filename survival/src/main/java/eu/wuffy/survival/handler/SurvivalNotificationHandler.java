package eu.wuffy.survival.handler;

import org.bukkit.ChatColor;

import eu.wuffy.core.handler.notification.NotificationHandler;
import eu.wuffy.survival.Survival;
import eu.wuffy.survival.config.ConfigNotification;
import eu.wuffy.synced.config.ConfigService;

public class SurvivalNotificationHandler extends NotificationHandler<Survival> {

	public SurvivalNotificationHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		ConfigNotification config = ConfigService.getConfig(ConfigNotification.class);
		config.messages.forEach(message -> this.messages.add(ChatColor.translateAlternateColorCodes('&', message)));
		this.delay = config.delay;

		super.onEnable();
	}
}