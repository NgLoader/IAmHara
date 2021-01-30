package eu.wuffy.core.npc.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import eu.wuffy.core.npc.NPC;

public class NPCDeletedEvent extends Event {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private final NPC npc;

	public NPCDeletedEvent(NPC npc) {
		this.npc = npc;
	}

	public NPC getNPC() {
		return this.npc;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}
