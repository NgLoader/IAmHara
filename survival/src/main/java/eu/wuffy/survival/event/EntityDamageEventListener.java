package eu.wuffy.survival.event;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.VanishHandler;

public class EntityDamageEventListener implements Listener {

	private final Survival core;
	private final VanishHandler vanishHandler;

	public EntityDamageEventListener(Survival core) {
		this.core = core;
		this.vanishHandler = this.core.getVanishHandler();
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			if (this.vanishHandler.isVanish((Player) event.getEntity())) {
				event.setCancelled(true);
			}
		}
	}

	public Survival getCore() {
		return this.core;
	}
}