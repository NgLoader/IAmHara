package eu.wuffy.core.util;

import java.lang.reflect.Field;

public class ReflectionUtil {

	public static void set(Object instance, String name, Object value) {
		set(instance.getClass(), instance, name, value);
	}

	public static void set(Class<?> clazz, Object instance, String name, Object value) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			field.set(instance, value);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Object get(Object instance, String name) {
		return get(instance.getClass(), instance, name);
	}

	public static Object get(Class<?> clazz, Object obj, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
