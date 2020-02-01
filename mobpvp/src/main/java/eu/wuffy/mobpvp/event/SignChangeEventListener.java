package eu.wuffy.mobpvp.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;

import eu.wuffy.mobpvp.MobPvP;
import eu.wuffy.mobpvp.handler.event.EventListener;
import eu.wuffy.mobpvp.kits.Kit;
import eu.wuffy.mobpvp.kits.KitHandler;

public class SignChangeEventListener extends EventListener {

	private KitHandler kitHandler;

	public SignChangeEventListener(MobPvP core) {
		super(core);
	}

	@Override
	public void onInit() {
		this.kitHandler = this.core.getKitHandler();
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("mobpvp") && event.getLine(1).equalsIgnoreCase("kit")) {
			Kit kit = this.kitHandler.searchKit(event.getLine(2));

			if (kit == null) {
				event.getPlayer().sendMessage(MobPvP.PREFIX + "Das §cKit §7konnte nichte gefunden werden§8.");
				return;
			}

			event.setLine(0, MobPvP.PREFIX_SIGN);
			event.setLine(1, kit.getType().name);
		}
	}
}