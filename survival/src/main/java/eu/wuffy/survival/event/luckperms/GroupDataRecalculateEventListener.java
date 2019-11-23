package eu.wuffy.survival.event.luckperms;

import java.util.function.Consumer;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.scoreboard.ScoreboardHandler;
import me.lucko.luckperms.api.event.group.GroupDataRecalculateEvent;

public class GroupDataRecalculateEventListener implements Consumer<GroupDataRecalculateEvent> {

	private final Survival core;
	private final ScoreboardHandler scoreboardHandler;

	public GroupDataRecalculateEventListener(Survival core) {
		this.core = core;
		this.scoreboardHandler = this.core.getScoreboardHandler();
	}

	@Override
	public void accept(GroupDataRecalculateEvent event) {
		this.scoreboardHandler.loadAllGroups();
	}

	public Survival getCore() {
		return this.core;
	}
}