package eu.wuffy.survival.event.luckperms;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.wuffy.survival.Survival;
import me.lucko.luckperms.api.event.user.track.UserPromoteEvent;

public class UserPromoteEventListener implements Consumer<UserPromoteEvent> {

	private Survival core;

	public UserPromoteEventListener(Survival core) {
		this.core = core;
	}

	@Override
	public void accept(UserPromoteEvent event) {
		Player player = Bukkit.getPlayer(event.getUser().getUuid());

		if (player != null && player.isOnline()) {
			this.core.getScoreboardHandler().removePlayerFromScoreboard(player);
			this.core.getScoreboardHandler().addPlayerToScoreboard(player, event.getApi().getGroup(event.getApi().getUser(event.getUser().getUuid()).getPrimaryGroup()));
		}
	}

	public Survival getCore() {
		return this.core;
	}
}