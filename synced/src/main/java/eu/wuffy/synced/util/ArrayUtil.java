package eu.wuffy.synced.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ArrayUtil {

	public static final List<String> EMPTY_ARRAY_LIST = Collections.unmodifiableList(new ArrayList<String>());

	public static <T> Object[] merge(T[] a, T[] b) {
		return Stream.of(a, b).flatMap(Stream::of).toArray();
	}
}