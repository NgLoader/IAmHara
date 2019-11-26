package eu.wuffy.survival.event.factions;

import org.bukkit.event.EventHandler;

import com.massivecraft.factions.event.FactionCreateEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.event.EventListener;

public class FactionCreateEventListener extends EventListener {

	public FactionCreateEventListener(Survival core) {
		super(core);
	}

	@EventHandler
	public void onFactionCreate(FactionCreateEvent event) {
	}
}