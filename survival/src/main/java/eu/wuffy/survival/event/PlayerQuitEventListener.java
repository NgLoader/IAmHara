package eu.wuffy.survival.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.wuffy.survival.Survival;

public class PlayerQuitEventListener implements Listener {

	private Survival core;

	public PlayerQuitEventListener(Survival core) {
		this.core = core;
	}

	@EventHandler
	public void onChat(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		event.setQuitMessage("§8[§c-§8] " + event.getPlayer().getDisplayName());

		this.core.getHomeHandler().unload(player.getUniqueId());
		this.core.getScoreboardHandler().removePlayerFromScoreboard(player);
	}

	public Survival getCore() {
		return this.core;
	}
}