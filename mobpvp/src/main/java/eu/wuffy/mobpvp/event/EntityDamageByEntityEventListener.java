package eu.wuffy.mobpvp.event;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.handler.DamageHandler;
import eu.wuffy.mobpvp.handler.event.EventListener;
import eu.wuffy.mobpvp.kits.KitHandler;

public class EntityDamageByEntityEventListener extends EventListener {

	private KitHandler kitHandler;
	private DamageHandler damageHandler;

	public EntityDamageByEntityEventListener(MobPvP core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.kitHandler = this.core.getKitHandler();
		this.damageHandler = this.core.getDamageHandler();
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();
			Player damager = null;

			if (event.getDamager().getType() == EntityType.PLAYER) {
				damager = (Player) event.getDamager();
			} else if (event.getDamager().getType() == EntityType.ARROW || event.getDamager().getType() == EntityType.SPECTRAL_ARROW) {
				ProjectileSource arrowShooter = ((Arrow) event.getDamager()).getShooter();

				if (arrowShooter instanceof Player) {
					damager = (Player) arrowShooter;
				} else {
					return;
				}
			} else {
				return;
			}

			if (this.kitHandler.getPlayerKit(player) == null || this.kitHandler.getPlayerKit(damager) == null) {
				event.setCancelled(true);
			}

			this.damageHandler.getDamageState(player).addDamage(damager, event.getFinalDamage());
		}
	}
}