package eu.wuffy.core.npc.npc.entity;

import org.bukkit.Location;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;

import eu.wuffy.core.Core;
import eu.wuffy.core.npc.NPCRegistry;
import eu.wuffy.core.npc.npc.type.NPCWaterAnimal;
import eu.wuffy.core.npc.wrapper.WrappedEntityId;

public class NPCDolphin extends NPCWaterAnimal {

	private static final Serializer VECTOR3F_SERIALIZER = WrappedDataWatcher.Registry.getVectorSerializer();

	public NPCDolphin(NPCRegistry<Core<?>> registry, Location location) {
		super(registry, 0, location, WrappedEntityId.DOLPHIN);
	}

	public void setTreasurePosition(Vector3F position) {
		this.setMetadata(15, VECTOR3F_SERIALIZER, position);
	}

	public void setCanFindTreasure(boolean canFind) {
		this.setMetadata(16, Boolean.class, canFind);
	}

	public void setHasFish(boolean hasFish) {
		this.setMetadata(17, Boolean.class, hasFish);
	}
}