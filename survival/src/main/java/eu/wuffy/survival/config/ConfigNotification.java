package eu.wuffy.survival.config;

import java.util.Arrays;
import java.util.List;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.config.Config;

@Config(path = Survival.CONFIG_FOLDER, name = "notification")
public class ConfigNotification {

	public long delay = 1000 * 60 * 30;
	public List<String> messages = Arrays.asList("&aHier könnte ihre werbung stehen");
}
