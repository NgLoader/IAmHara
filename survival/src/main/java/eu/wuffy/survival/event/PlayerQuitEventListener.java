package eu.wuffy.survival.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.TreeFellerHandler;
import eu.wuffy.survival.handler.VanishHandler;
import eu.wuffy.survival.handler.home.HomeHandler;
import eu.wuffy.survival.handler.scoreboard.ScoreboardHandler;

public class PlayerQuitEventListener implements Listener {

	private final Survival core;
	private final HomeHandler homeHandler;
	private final ScoreboardHandler scoreboardHandler;
	private final VanishHandler vanishHandler;
	private final TreeFellerHandler treeFellerHandler;

	public PlayerQuitEventListener(Survival core) {
		this.core = core;
		this.homeHandler = this.core.getHomeHandler();
		this.scoreboardHandler = this.core.getScoreboardHandler();
		this.vanishHandler = this.core.getVanishHandler();
		this.treeFellerHandler = this.core.getTreeFellerHandler();
	}

	@EventHandler
	public void onChat(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		event.setQuitMessage("§8[§c-§8] " + event.getPlayer().getDisplayName());

		this.vanishHandler.onPlayerQuit(player);
		this.scoreboardHandler.onPlayerQuit(player);
		this.treeFellerHandler.onPlayerQuit(player);
		this.homeHandler.unload(player.getUniqueId());
	}

	public Survival getCore() {
		return this.core;
	}
}