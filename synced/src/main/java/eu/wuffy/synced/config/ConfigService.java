package eu.wuffy.synced.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.wuffy.synced.config.typeadapter.TypeAdapterUUID;

public class ConfigService {

	private static final Map<Class<?>, Object> CONFIGS = new ConcurrentHashMap<>();
	private static final ReadWriteLock LOCK = new ReentrantReadWriteLock(true);

	private static final GsonBuilder GSON_BUILDER = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping();
	private static Gson GSON;

	public static <T> void addTypeAdapter(ITypeAdapter<T> adapter) {
		GSON_BUILDER.registerTypeAdapter(adapter.getType(), adapter);

		if (GSON != null) {
			GSON = GSON_BUILDER.create();
		}
	}

	static {
		ConfigService.addTypeAdapter(new TypeAdapterUUID());
		GSON = GSON_BUILDER.create();
	}

	public static <T> void loadConfig(Class<? extends T> configClass) {
		if (configClass == null) {
			throw new NullPointerException("Config class was null");
		}

		Config annotation = configClass.getDeclaredAnnotation(Config.class);
		if (annotation == null) {
			throw new NullPointerException(String.format("'%s' has not the annotation '%s'", configClass.getSimpleName(), Config.class.getSimpleName()));
		}

		Path path = getPath(annotation);
		if (Files.notExists(path)) {
			try {
				Files.createDirectories(path.getParent());

				try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
					T instance = configClass.getDeclaredConstructor().newInstance();
					GSON.toJson(instance, bufferedWriter);
				} catch (Exception e) {
//					LOGGER.error(String.format("Failed to write file '%s'", annotation.path()), e);
					e.printStackTrace();
					return;
				}
			} catch (IOException e) {
//				LOGGER.error(String.format("Failed to create config '%s'", configClass.getSimpleName()), e);
				e.printStackTrace();
				return;
			}
		}

		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			T config = GSON.fromJson(reader, configClass);
			if (config == null) {
				throw new NullPointerException(String.format("Error by reading json file '%s'", annotation.path()));
			}

			ConfigService.LOCK.writeLock().lock();
			ConfigService.CONFIGS.put(configClass, config);
		} catch (Exception e) {
//			LOGGER.error(String.format("Error loading file '%s'", annotation.path()), e);
			e.printStackTrace();
		} finally {
			ConfigService.LOCK.writeLock().unlock();
		}
	}

	public static void saveConfig(Class<?> configClass) {
		Object config = ConfigService.getConfig(configClass);
		ConfigService.saveConfig(config);
	}

	public static void saveConfig(Object config) {
		if (config == null) {
			throw new NullPointerException("Config class was null");
		}

		Config annotation = getConfigSettings(config.getClass());
		Path path = Paths.get(annotation.path());

		if (Files.notExists(path)) {
			try {
				Files.createDirectories(path.getParent());
			} catch (IOException e) {
//				LOGGER.error(String.format("Failed to config directory '%s'", annotation.path()), e);
				e.printStackTrace();
				return;
			}
		}

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
			GSON.toJson(config, bufferedWriter);
		} catch (IOException e) {
//			LOGGER.error(String.format("Failed to write file '%s'", annotation.path()), e);
			e.printStackTrace();
		}
	}

	public static <T> T getConfig(Class<T> configClass) {
		if (configClass == null) {
			throw new NullPointerException("Config class was null");
		}

		ConfigService.LOCK.readLock().lock();
		if (!ConfigService.CONFIGS.containsKey(configClass)) {
			try {
				ConfigService.LOCK.readLock().unlock();
				ConfigService.loadConfig(configClass);
			} finally {
				ConfigService.LOCK.readLock().lock();
			}
		}

		T config = configClass.cast(ConfigService.CONFIGS.get(configClass));
		ConfigService.LOCK.readLock().unlock();
		return config;
	}

	public static <T> T reloadConfig(Class<T> configClass) {
		ConfigService.removeConfig(configClass);
		return ConfigService.getConfig(configClass);
	}
	public static <T> T removeConfig(T config) {
		ConfigService.removeConfig(config.getClass());
		return config;
	}

	public static <T> T removeConfig(Class<T> configClass) {
		ConfigService.LOCK.writeLock().lock();
		Object config = ConfigService.CONFIGS.remove(configClass);
		ConfigService.LOCK.writeLock().unlock();

		return config != null ? configClass.cast(config) : null;
	}

	public static void removeAllConfigs() {
		ConfigService.CONFIGS.clear();
	}

	public static Config getConfigSettings(Class<?> clazz) {
		Config config = clazz.getDeclaredAnnotation(Config.class);
		if (config == null) {
			throw new NullPointerException(String.format("'%s' has not the annotation '%s'", clazz.getSimpleName(), Config.class.getSimpleName()));
		}
		return config;
	}

	public static Path getPath(Config config) {
		return Paths.get(String.format("./plugins/%s/%s.json", config.path(), config.name()));
	}
}