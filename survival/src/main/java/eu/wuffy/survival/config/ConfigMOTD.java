package eu.wuffy.survival.config;

import org.bukkit.ChatColor;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.config.Config;

@Config(path = Survival.CONFIG_FOLDER, name = "motd")
public class ConfigMOTD {

	public String firstLine = "&aHier könnte ihre";
	public String secondLine = "&5Motd &6stehen&8.";

	public String getMotd() {
		return ChatColor.translateAlternateColorCodes('&', String.format("%s\n%s", this.firstLine, this.secondLine));
	}
}