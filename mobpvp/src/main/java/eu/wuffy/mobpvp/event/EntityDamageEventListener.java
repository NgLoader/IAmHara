package eu.wuffy.mobpvp.event;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.handler.event.EventListener;
import eu.wuffy.mobpvp.kits.KitHandler;

public class EntityDamageEventListener extends EventListener {

	private KitHandler kitHandler;

	public EntityDamageEventListener(MobPvP core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.kitHandler = this.core.getKitHandler();
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();

			if (this.kitHandler.getPlayerKit(player) == null) {
				event.setCancelled(true);
			}
		}
	}
}