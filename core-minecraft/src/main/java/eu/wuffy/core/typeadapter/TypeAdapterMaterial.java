package eu.wuffy.core.typeadapter;

import java.lang.reflect.Type;

import org.bukkit.Material;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import eu.wuffy.synced.config.ITypeAdapter;

public class TypeAdapterMaterial implements ITypeAdapter<Material> {

	@Override
	public JsonElement serialize(Material src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.name());
	}

	@Override
	public Material deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return Material.valueOf(json.getAsString());
	}

	@Override
	public Class<Material> getType() {
		return Material.class;
	}
}