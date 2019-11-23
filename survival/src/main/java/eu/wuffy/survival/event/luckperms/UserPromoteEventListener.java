package eu.wuffy.survival.event.luckperms;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.VanishHandler;
import eu.wuffy.survival.handler.scoreboard.ScoreboardHandler;
import me.lucko.luckperms.api.event.user.track.UserPromoteEvent;

public class UserPromoteEventListener implements Consumer<UserPromoteEvent> {

	private final Survival core;
	private final ScoreboardHandler scoreboardHandler;
	private final VanishHandler vanishHandler;

	public UserPromoteEventListener(Survival core) {
		this.core = core;
		this.scoreboardHandler = this.core.getScoreboardHandler();
		this.vanishHandler = this.core.getVanishHandler();
	}

	@Override
	public void accept(UserPromoteEvent event) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUser().getUuid());

		if (player != null && player.isOnline()) {
			this.scoreboardHandler.onPlayerQuit(player);
			this.scoreboardHandler.onPlayerJoin(player.getPlayer(), event.getApi().getGroup(event.getApi().getUserSafe(event.getUser().getUuid()).get().getPrimaryGroup()));

			this.vanishHandler.checkTabVisibilityForPlayer(player.getPlayer(), true);
		}
	}

	public Survival getCore() {
		return this.core;
	}
}