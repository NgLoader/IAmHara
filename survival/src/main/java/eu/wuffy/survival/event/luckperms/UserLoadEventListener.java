package eu.wuffy.survival.event.luckperms;

import java.util.function.Consumer;

import org.bukkit.Bukkit;

import eu.wuffy.survival.Survival;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.event.user.UserLoadEvent;

public class UserLoadEventListener implements Consumer<UserLoadEvent> {

	private Survival core;

	public UserLoadEventListener(Survival core) {
		this.core = core;
	}

	@Override
	public void accept(UserLoadEvent event) {
		Group group = event.getApi().getGroup(event.getUser().getPrimaryGroup());

		this.core.getScoreboardHandler().addPlayerToScoreboard(Bukkit.getPlayer(event.getUser().getUuid()), group);
	}

	public Survival getCore() {
		return this.core;
	}
}