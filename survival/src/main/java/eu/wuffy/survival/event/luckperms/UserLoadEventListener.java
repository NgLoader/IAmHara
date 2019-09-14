package eu.wuffy.survival.event.luckperms;

import java.util.function.Consumer;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.ScoreboardHandler;
import me.lucko.luckperms.api.event.user.UserLoadEvent;

public class UserLoadEventListener implements Consumer<UserLoadEvent> {

	private final Survival core;
	private final ScoreboardHandler scoreboardHandler;

	public UserLoadEventListener(Survival core) {
		this.core = core;
		this.scoreboardHandler = this.core.getScoreboardHandler();
	}

	@Override
	public void accept(UserLoadEvent event) {
		this.scoreboardHandler.onPlayerJoin(event.getUser().getName(), event.getApi().getGroupSafe(event.getUser().getPrimaryGroup()).get());
	}

	public Survival getCore() {
		return this.core;
	}
}