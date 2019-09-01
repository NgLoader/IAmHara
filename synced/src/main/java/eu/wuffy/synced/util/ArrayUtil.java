package eu.wuffy.synced.util;

import java.util.stream.Stream;

public class ArrayUtil {

	public static <T> Object[] merge(T[] a, T[] b) {
		return Stream.of(a, b).flatMap(Stream::of).toArray();
	}
}