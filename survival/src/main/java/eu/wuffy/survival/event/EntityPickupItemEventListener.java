package eu.wuffy.survival.event;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.VanishHandler;

public class EntityPickupItemEventListener implements Listener {

	private final Survival core;
	private final VanishHandler vanishHandler;

	public EntityPickupItemEventListener(Survival core) {
		this.core = core;
		this.vanishHandler = this.core.getVanishHandler();
	}

	@EventHandler
	public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
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