package eu.wuffy.survival.event.bukkit;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.VanishHandler;
import eu.wuffy.survival.handler.event.EventListener;

public class EntityPickupItemEventListener extends EventListener {

	private VanishHandler vanishHandler;

	public EntityPickupItemEventListener(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.vanishHandler = this.getCore().getVanishHandler();
	}

	@EventHandler
	public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();

			if (this.vanishHandler.isVanish(player)) {
				event.setCancelled(true);
			}
		}
	}
}