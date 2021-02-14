package eu.wuffy.synced.config;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface ITypeAdapter <T> extends JsonSerializer<T>, JsonDeserializer<T> {

	public Class<T> getType();
}