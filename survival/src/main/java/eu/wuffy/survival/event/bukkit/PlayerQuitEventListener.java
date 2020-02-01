package eu.wuffy.survival.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.wuffy.core.handler.ChatHandler;
import eu.wuffy.core.scoreboard.ScoreboardHandler;
import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.TreeFellerHandler;
import eu.wuffy.survival.handler.VanishHandler;
import eu.wuffy.survival.handler.event.EventListener;
import eu.wuffy.survival.handler.home.HomeHandler;

public class PlayerQuitEventListener extends EventListener {

	private HomeHandler homeHandler;
	private ScoreboardHandler scoreboardHandler;
	private ChatHandler chatHandler;
	private VanishHandler vanishHandler;
	private TreeFellerHandler treeFellerHandler;

	public PlayerQuitEventListener(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.homeHandler = this.getCore().getHomeHandler();
		this.scoreboardHandler = this.getCore().getScoreboardHandler();
		this.chatHandler = this.getCore().getChatHandler();
		this.vanishHandler = this.getCore().getVanishHandler();
		this.treeFellerHandler = this.getCore().getTreeFellerHandler();
	}

	@EventHandler
	public void onChat(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		event.setQuitMessage("§8[§c-§8] " + event.getPlayer().getDisplayName());

		this.vanishHandler.onPlayerQuit(player);
		this.scoreboardHandler.removePlayerScoreboard(player);
		this.chatHandler.removeMessagePattern(player);
		this.treeFellerHandler.onPlayerQuit(player);
		this.homeHandler.unload(player.getUniqueId());
	}
}