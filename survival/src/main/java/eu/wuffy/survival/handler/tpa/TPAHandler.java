package eu.wuffy.survival.handler.tpa;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.synced.IHandler;

public class TPAHandler extends IHandler<Survival> {

	private final Map<Player, TPARequests> requests = new HashMap<>();

	private int taskId;

	public TPAHandler(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		Bukkit.getScheduler().runTaskTimer(this.core, () -> {
			this.requests.values().forEach(request -> request.run());
		}, 20, 20);
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTask(this.taskId);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		TPARequests requests = this.requests.remove(event.getPlayer());

		if (requests != null) {
			requests.destroy();
		}
	}

	public TPARequests getRequests(Player player) {
		TPARequests requests = this.requests.get(player);

		if (requests == null) {
			requests = new TPARequests(player);
			this.requests.put(player, requests);
		}

		return requests;
	}
}