package eu.wuffy.core.npc.npc.entity;

import org.bukkit.Location;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.NPCRegistry;
import eu.wuffy.core.npc.npc.type.NPCAmbientCreature;
import eu.wuffy.core.npc.wrapper.EntityFlag;
import eu.wuffy.core.npc.wrapper.WrappedEntityId;

public class NPCBat extends NPCAmbientCreature {

	public NPCBat(NPCRegistry<Core<?>> registry, Location location) {
		super(registry, 0, location, WrappedEntityId.BAT);
	}

	public void setIsHanging(boolean hanging) {
		this.setFlag(EntityFlag.BAT_IS_HANGING, hanging);
	}
}