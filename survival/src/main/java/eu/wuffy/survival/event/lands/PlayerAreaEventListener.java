package eu.wuffy.survival.event.lands;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.event.EventListener;
import me.angeschossen.lands.api.integration.LandsIntegration;

public class PlayerAreaEventListener extends EventListener {

	private LandsIntegration landsIntegration;

	public PlayerAreaEventListener(Survival core) {
		super(core);
	}

	@Override
	public void onEnable() {
		this.landsIntegration = this.core.getLandsIntegration();
	}
}
