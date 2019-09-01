package eu.wuffy.core.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSUtil {

	public static int getPlayerPing(Player player) {
		try {
			String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
			Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".entity.CraftPlayer");
			Object handle = craftPlayer.getMethod("getHandle").invoke(player);
			Integer ping = (Integer) handle.getClass().getDeclaredField("ping").get(handle);

			return ping.intValue();
		} catch (Exception e) {
			return -1;
		}
	}
}