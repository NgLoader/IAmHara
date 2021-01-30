package eu.wuffy.core.npc.npc.entity;

import org.bukkit.Location;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.NPCRegistry;
import eu.wuffy.core.npc.npc.type.NPCAnimal;
import eu.wuffy.core.npc.wrapper.EntityFlag;
import eu.wuffy.core.npc.wrapper.WrappedEntityId;
import eu.wuffy.core.npc.wrapper.WrappedEnumColor;

public class NPCSheep extends NPCAnimal {

	public NPCSheep(NPCRegistry<Core<?>> registry, Location location) {
		super(registry, 0.62d, location, WrappedEntityId.SHEEP);
	}

	public void setSheared(boolean sheared) {
		this.setFlag(EntityFlag.SHEEP_IS_SHEARED, sheared);
	}

	public void setColor(WrappedEnumColor color) {
		this.setMetadata(EntityFlag.SHEEP_COLOR_ID.getIndex(), Byte.class, color.getColorIndex());
	}
}