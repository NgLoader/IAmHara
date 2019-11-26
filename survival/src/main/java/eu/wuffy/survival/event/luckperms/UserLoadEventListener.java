package eu.wuffy.survival.event.luckperms;

import java.util.function.Consumer;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.event.EventListener;
import me.lucko.luckperms.api.event.user.UserLoadEvent;

public class UserLoadEventListener extends EventListener implements Consumer<UserLoadEvent> {

	public UserLoadEventListener(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.getCore().getLuckPermsHandler().getApi().get().getEventBus().subscribe(UserLoadEvent.class, this);
	}

	@Override
	public void accept(UserLoadEvent event) { }
}