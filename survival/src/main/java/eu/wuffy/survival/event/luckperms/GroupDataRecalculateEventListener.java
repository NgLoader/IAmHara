package eu.wuffy.survival.event.luckperms;

import java.util.function.Consumer;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.event.EventListener;
import eu.wuffy.survival.handler.scoreboard.ScoreboardHandler;
import me.lucko.luckperms.api.event.group.GroupDataRecalculateEvent;

public class GroupDataRecalculateEventListener extends EventListener implements Consumer<GroupDataRecalculateEvent> {

	private ScoreboardHandler scoreboardHandler;

	public GroupDataRecalculateEventListener(Survival core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.scoreboardHandler = this.getCore().getScoreboardHandler();
	}

	@Override
	public void onEnable() {
		this.getCore().getLuckPermsHandler().getApi().get().getEventBus().subscribe(GroupDataRecalculateEvent.class, this);
	}

	@Override
	public void accept(GroupDataRecalculateEvent event) {
		this.scoreboardHandler.loadAllGroups();
	}
}