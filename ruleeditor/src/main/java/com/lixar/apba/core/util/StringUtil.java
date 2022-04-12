package com.lixar.apba.core.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.lixar.apba.core.util.GameConstants.*;

import org.apache.commons.lang.StringUtils;

public class StringUtil {
	private static final Pattern removeWhitespacePattern = Pattern.compile("\\s+");
	private static final Pattern startingDigitPattern = Pattern.compile("^\\d+");

	/**
	 * Strips all whitespace except spaces, removes any occurrence of double-whitespace (e..g space next to a space) and trims the final output.
	 *
	 * @param s The string to clean - if null, returns an empty string
	 * @return the cleaned String
	 */
	public static String cleanString(String s) {
		s = StringUtils.trimToEmpty(s);
		if (s.length() == 0) {
			return s;
		}

		return removeWhitespacePattern.matcher(s).replaceAll(EMPTY_SPACE);
	}

	public static String toCSV(List<? extends Number> list) {
		return list.stream().map(Number::toString).collect(Collectors.joining(COMMA));
	}

	public static String substr(String s, int start, int length) {
		return StringUtils.substring(s, start, start + length);
	}

	/**
	 * Simulation of PHP implicit string to boolean type cast
	 *
	 * @param s string to cast
	 * @return if param is null, empty (or blanks) or "0"
	 */
	public static boolean phpStrToBool(String s) {
		if ((s == null) || (EMPTY_STRING.equals(s.trim())) || (INT_STR_ZERO.equals(s))) {
			return false;
		}
		return true;
	}

	public static int getStartingIntOrZero(String s) {
		if (s == null) {
			return 0;
		}

		Matcher firstNumberMatcher = startingDigitPattern.matcher(s.trim());
		return firstNumberMatcher.find() ? PHPHelper.toInt(firstNumberMatcher.group()) : 0;
	}
}
