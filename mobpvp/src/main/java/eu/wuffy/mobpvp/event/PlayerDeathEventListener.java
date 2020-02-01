package eu.wuffy.mobpvp.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.handler.DamageHandler;
import eu.wuffy.mobpvp.handler.event.EventListener;
import eu.wuffy.mobpvp.kits.Kit;
import eu.wuffy.mobpvp.kits.KitHandler;

public class PlayerDeathEventListener extends EventListener {

	private KitHandler kitHandler;
	private DamageHandler damageHandler;

	public PlayerDeathEventListener(MobPvP core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.kitHandler = this.core.getKitHandler();
		this.damageHandler = this.core.getDamageHandler();
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = (Player) event.getEntity();

		event.setDeathMessage("");
		event.setNewExp(0);
		event.setNewLevel(0);
		event.setNewTotalExp(0);
		event.setKeepInventory(true);

		Kit kit = this.kitHandler.getPlayerKit(player);
		if (kit != null) {
			this.kitHandler.remove(player);
			this.damageHandler.getDamageState(player).finish();
		}
	}
}
