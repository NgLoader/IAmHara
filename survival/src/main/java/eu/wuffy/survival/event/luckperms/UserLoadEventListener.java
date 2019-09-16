package eu.wuffy.survival.event.luckperms;

import java.util.function.Consumer;

import eu.wuffy.survival.Survival;
import me.lucko.luckperms.api.event.user.UserLoadEvent;

public class UserLoadEventListener implements Consumer<UserLoadEvent> {

	private final Survival core;

	public UserLoadEventListener(Survival core) {
		this.core = core;
	}

	@Override
	public void accept(UserLoadEvent event) { }

	public Survival getCore() {
		return this.core;
	}
}