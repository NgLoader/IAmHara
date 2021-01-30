package eu.wuffy.core.npc.npc.type;

import org.bukkit.Location;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.NPCRegistry;
import eu.wuffy.core.npc.wrapper.WrappedEntityId;

public class NPCAmbientCreature extends NPCMob {

	public NPCAmbientCreature(NPCRegistry<Core<?>> registry, double eyeHeight, Location location, WrappedEntityId id) {
		super(registry, eyeHeight, location, id);
	}
}