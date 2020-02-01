package eu.wuffy.survival.event.bukkit;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.wuffy.core.handler.ChatHandler;
import eu.wuffy.core.scoreboard.ScoreboardHandler;
import eu.wuffy.survival.Survival;
import eu.wuffy.survival.database.SurvivalDatabase;
import eu.wuffy.survival.handler.VanishHandler;
import eu.wuffy.survival.handler.event.EventListener;
import eu.wuffy.survival.handler.home.HomeHandler;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.UserManager;

public class PlayerJoinEventListener extends EventListener {

	private SurvivalDatabase database;
	private HomeHandler homeHandler;
	private VanishHandler vanishHandler;
	private ScoreboardHandler scoreboardHandler;
	private LuckPerms luckPerms;
	private UserManager userManager;
	private ChatHandler chatHandler;

	public PlayerJoinEventListener(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.database = this.getCore().getDatabase();
		this.homeHandler = this.getCore().getHomeHandler();
		this.vanishHandler = this.getCore().getVanishHandler();
		this.scoreboardHandler = this.getCore().getScoreboardHandler();
		this.chatHandler = this.getCore().getChatHandler();
	}

	@Override
	public void onEnable() {
		this.luckPerms = LuckPermsProvider.get();
		this.userManager = this.luckPerms.getUserManager();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		event.setJoinMessage("§8[§a+§8] " + player.getDisplayName());

		try {
			this.scoreboardHandler.getTeams().forEach(team -> team.sendCreatePacket(player));
			this.userManager.loadUser(player.getUniqueId()).thenAcceptAsync(user -> this.scoreboardHandler.getPlayerScoreboard(player).joinTeam(user.getPrimaryGroup()));
			this.chatHandler.updateMessagePattern(player);

			this.database.getPlayerId(player.getUniqueId());
			this.homeHandler.load(player.getUniqueId());
			this.vanishHandler.onPlayerJoin(player);
		} catch (SQLException e) {
			e.printStackTrace();
			player.kickPlayer(Survival.PREFIX + "§7Es ist ein §cfehler §7beim laden deiner §cspielerdaten §7aufgetreten§8.");
		}
	}
}