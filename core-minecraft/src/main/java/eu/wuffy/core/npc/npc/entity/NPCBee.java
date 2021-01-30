package eu.wuffy.core.npc.npc.entity;

import org.bukkit.Location;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.NPCRegistry;
import eu.wuffy.core.npc.npc.type.NPCAnimal;
import eu.wuffy.core.npc.wrapper.EntityFlag;
import eu.wuffy.core.npc.wrapper.WrappedEntityId;

public class NPCBee extends NPCAnimal {

	public NPCBee(NPCRegistry<Core<?>> registry, Location location) {
		super(registry, 0, location, WrappedEntityId.BEE);
	}

	public void setAngry(boolean angry) {
		this.setFlag(EntityFlag.BEE_IS_ANGRY, angry);
	}

	public void setHasStung(boolean has) {
		this.setFlag(EntityFlag.BEE_HAS_STUNG, has);
	}

	public void setHasNectar(boolean has) {
		this.setFlag(EntityFlag.BEE_HAS_NECTAR, has);
	}

	public void setAngerTime(int ticks) {
		this.setMetadata(17, Integer.class, ticks);
	}
}