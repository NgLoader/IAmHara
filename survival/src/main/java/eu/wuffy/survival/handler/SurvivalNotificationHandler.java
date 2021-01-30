package eu.wuffy.survival.handler;

import eu.wuffy.core.handler.notification.NotificationHandler;
import eu.wuffy.survival.Survival;

public class SurvivalNotificationHandler extends NotificationHandler<Survival> {

	public SurvivalNotificationHandler(Survival core) {
		super(core);

		this.messages.add("§aHier könnte ihre werbung stehen!?");
	}
}