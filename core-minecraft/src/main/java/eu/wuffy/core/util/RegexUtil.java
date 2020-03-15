package eu.wuffy.core.util;

public class RegexUtil {

	private static final String PATTERN_NUMBER = "^[0-9]+(\\.[0-9]+)?$";

	public static boolean isNumber(String string) {
		return string.matches(PATTERN_NUMBER);
	}
}