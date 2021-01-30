package eu.wuffy.core.npc.npc.type;

import org.bukkit.Location;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.NPCRegistry;
import eu.wuffy.core.npc.wrapper.WrappedEntityId;

public class NPCAgeableMob extends NPCMob {

	public NPCAgeableMob(NPCRegistry<Core<?>> registry, double eyeHeight, Location location, WrappedEntityId id) {
		super(registry, eyeHeight, location, id);
	}

	public void setIsBaby(boolean baby) {
		this.setMetadata(15, Boolean.class, baby);
	}
}