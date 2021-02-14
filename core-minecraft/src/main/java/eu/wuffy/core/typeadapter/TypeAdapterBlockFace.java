package eu.wuffy.core.typeadapter;

import java.lang.reflect.Type;

import org.bukkit.block.BlockFace;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import eu.wuffy.synced.config.ITypeAdapter;

public class TypeAdapterBlockFace implements ITypeAdapter<BlockFace> {

	@Override
	public JsonElement serialize(BlockFace src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.name());
	}

	@Override
	public BlockFace deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return BlockFace.valueOf(json.getAsString());
	}

	@Override
	public Class<BlockFace> getType() {
		return BlockFace.class;
	}
}