package eu.wuffy.survival.event;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.database.SurvivalDatabase;
import eu.wuffy.survival.handler.ScoreboardHandler;
import eu.wuffy.survival.handler.VanishHandler;
import eu.wuffy.survival.home.HomeHandler;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.manager.UserManager;

public class PlayerJoinEventListener implements Listener {

	private final Survival core;
	private final SurvivalDatabase database;
	private final HomeHandler homeHandler;
	private final VanishHandler vanishHandler;
	private final ScoreboardHandler scoreboardHandler;
	private final LuckPermsApi luckPermsApi;
	private final UserManager userManager;

	public PlayerJoinEventListener(Survival core) {
		this.core = core;
		this.database = this.core.getDatabase();
		this.homeHandler = this.core.getHomeHandler();
		this.vanishHandler = this.core.getVanishHandler();
		this.scoreboardHandler = this.core.getScoreboardHandler();
		this.luckPermsApi = this.core.getLuckPermsApi();
		this.userManager = this.luckPermsApi.getUserManager();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		event.setJoinMessage("§8[§a+§8] " + player.getDisplayName());

		try {
			this.userManager.loadUser(player.getUniqueId()).thenAcceptAsync(user -> this.scoreboardHandler.onPlayerJoin(player, this.luckPermsApi.getGroup(user.getPrimaryGroup())));

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