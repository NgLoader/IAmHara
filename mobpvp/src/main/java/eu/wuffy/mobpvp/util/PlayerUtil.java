package eu.wuffy.mobpvp.util;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class PlayerUtil {

	public static void resetPlayer(Player player) {
		player.getInventory().clear();
		player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
		player.setTotalExperience(0);
		player.setExhaustion(0);
		player.setExp(0);
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		player.setAllowFlight(false);
		player.resetTitle();
		player.resetPlayerWeather();
		player.resetPlayerTime();
		player.setFlying(false);
		player.setSwimming(false);
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);
		player.setFoodLevel(20);
		player.setHealth(20);
		player.setHealthScaled(false);
		player.setGlowing(false);
	}
}