package eu.wuffy.survival.event.factions;

import org.bukkit.event.EventHandler;

import com.massivecraft.factions.event.FPlayerEnteredFactionEvent;

import eu.wuffy.survival.Survival;
import eu.wuffy.survival.handler.event.EventListener;

public class FPlayerEnteredFactionEventListener extends EventListener {

	public FPlayerEnteredFactionEventListener(Survival core) {
		super(core);
	}

	// When a player enter a new area like wildness -> TeamWuffy
	@EventHandler
	public void onFPlayerEnteredFaction(FPlayerEnteredFactionEvent event) { }
}