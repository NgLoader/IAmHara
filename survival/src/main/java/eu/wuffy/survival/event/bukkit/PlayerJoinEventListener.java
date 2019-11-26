package eu.wuffy.survival.event.bukkit;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.database.SurvivalDatabase;
import eu.wuffy.survival.handler.VanishHandler;
import eu.wuffy.survival.handler.event.EventListener;
import eu.wuffy.survival.handler.home.HomeHandler;
import eu.wuffy.survival.handler.scoreboard.ScoreboardHandler;
import me.lucko.luckperms.api.LuckPermsApi;
import me.lucko.luckperms.api.manager.UserManager;

public class PlayerJoinEventListener extends EventListener {

	private SurvivalDatabase database;
	private HomeHandler homeHandler;
	private VanishHandler vanishHandler;
	private ScoreboardHandler scoreboardHandler;
	private LuckPermsApi luckPermsApi;
	private UserManager userManager;

	public PlayerJoinEventListener(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.database = this.getCore().getDatabase();
		this.homeHandler = this.getCore().getHomeHandler();
		this.vanishHandler = this.getCore().getVanishHandler();
		this.scoreboardHandler = this.getCore().getScoreboardHandler();
	}

	@Override
	public void onEnable() {
		this.luckPermsApi = this.getCore().getLuckPermsHandler().getApi().get();
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
}