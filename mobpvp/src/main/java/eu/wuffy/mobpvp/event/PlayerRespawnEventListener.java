package eu.wuffy.mobpvp.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.handler.LocationHandler;
import eu.wuffy.mobpvp.handler.event.EventListener;

public class PlayerRespawnEventListener extends EventListener {

	private LocationHandler locationHandler;

	public PlayerRespawnEventListener(MobPvP core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.locationHandler = this.core.getLocationHandler();
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(this.locationHandler.get("spawn"));
	}
}