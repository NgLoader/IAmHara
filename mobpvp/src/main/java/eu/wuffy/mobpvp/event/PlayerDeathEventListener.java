package eu.wuffy.mobpvp.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.handler.event.EventListener;
import eu.wuffy.mobpvp.kits.Kit;
import eu.wuffy.mobpvp.kits.KitHandler;

public class PlayerDeathEventListener extends EventListener {

	private KitHandler kitHandler;

	public PlayerDeathEventListener(MobPvP core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.kitHandler = this.core.getKitHandler();
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = (Player) event.getEntity();

		event.setDeathMessage("");
		event.setNewExp(0);
		event.setNewLevel(0);
		event.setNewTotalExp(0);
		event.getDrops().clear();

		Kit kit = this.kitHandler.getPlayerKit(player);
		if (kit != null) {
			this.kitHandler.remove(player);
		}

		Bukkit.getScheduler().runTaskLater(this.core, new Runnable() {
			
			@Override
			public void run() {
				if (player != null && player.isOnline()) {
					player.spigot().respawn();
				}
			}
		}, 20);
	}
}
