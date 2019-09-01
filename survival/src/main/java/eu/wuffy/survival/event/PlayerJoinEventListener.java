package eu.wuffy.survival.event;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.wuffy.core.util.RankUtil;
import eu.wuffy.survival.Survival;

public class PlayerJoinEventListener implements Listener {

	private Survival core;

	public PlayerJoinEventListener(Survival core) {
		this.core = core;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		event.setJoinMessage("§8[§a+§8] " + player.getDisplayName());

		try {
			this.core.getDatabase().getPlayerId(player.getUniqueId());
			this.core.getHomeHandler().load(player.getUniqueId());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.core.getScoreboardHandler().addPlayerToScoreboard(player, RankUtil.getRankByPermission(player));
	}

	public Survival getCore() {
		return this.core;
	}
}