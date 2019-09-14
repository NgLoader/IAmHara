package eu.wuffy.survival.event;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.database.SurvivalDatabase;
import eu.wuffy.survival.handler.VanishHandler;
import eu.wuffy.survival.home.HomeHandler;

public class PlayerJoinEventListener implements Listener {

	private final Survival core;
	private final SurvivalDatabase database;
	private final HomeHandler homeHandler;
	private final VanishHandler vanishHandler;

	public PlayerJoinEventListener(Survival core) {
		this.core = core;
		this.database = this.core.getDatabase();
		this.homeHandler = this.core.getHomeHandler();
		this.vanishHandler = this.core.getVanishHandler();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		event.setJoinMessage("§8[§a+§8] " + player.getDisplayName());

		try {
			this.database.getPlayerId(player.getUniqueId());
			this.homeHandler.load(player.getUniqueId());
			this.vanishHandler.onPlayerJoin(player);
		} catch (SQLException e) {
			e.printStackTrace();
			player.kickPlayer(Survival.PREFIX + "§7Es ist ein §cfehler §7beim laden deiner §cspielerdaten §7aufgetreten§8.");
		}
	}

	public Survival getCore() {
		return this.core;
	}
}