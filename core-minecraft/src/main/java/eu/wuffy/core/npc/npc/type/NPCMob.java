package eu.wuffy.core.npc.npc.type;

import org.bukkit.Location;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.NPCRegistry;
import eu.wuffy.core.npc.npc.NPCSpawnEntityLiving;
import eu.wuffy.core.npc.wrapper.EntityFlag;
import eu.wuffy.core.npc.wrapper.WrappedEntityId;

public class NPCMob extends NPCSpawnEntityLiving {

	public NPCMob(NPCRegistry<Core<?>> registry, double eyeHeight, Location location, WrappedEntityId id) {
		super(registry, eyeHeight, location, id);
	}

	public void setNoAI(boolean noAI) {
		this.setFlag(EntityFlag.MOB_NO_AI, noAI);
	}

	public void setIsLeftHanded(boolean leftHanded) {
		this.setFlag(EntityFlag.MOB_IS_LEFT_HANDED, leftHanded);
	}

	public void setIsAgressive(boolean agressive) {
		this.setFlag(EntityFlag.MOB_IS_AGRESSIVE, agressive);
	}
}