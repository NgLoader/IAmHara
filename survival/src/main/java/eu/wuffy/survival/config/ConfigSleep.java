package eu.wuffy.survival.config;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.config.Config;

@Config(path = Survival.CONFIG_FOLDER, name = "sleep")
public class ConfigSleep {

	public int sleepPercent = 60;
}