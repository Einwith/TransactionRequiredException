package com.lixar.apba.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static com.lixar.apba.core.util.GameConstants.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class PHPHelper {
	public static int time() {
		return ((Long) Instant.now().getEpochSecond()).intValue();
	}

	public static boolean isEqualToZero(String s) {
		return !StringUtil.phpStrToBool(s);
	}

	public static boolean isNotEqualToZero(String s) {
		return StringUtil.phpStrToBool(s);
	}

	public static int toInt(String s) {
		// placing here for now so we can expand to handle other weird php cases
		return NumberUtils.toInt(s);
	}

	public static int toInt(Integer i) {
		return i == null ? 0 : i;
	}
	
	public static int toInt(Boolean b) {
		return Boolean.TRUE.equals(b) ? 1 : 0;
	}

	public static BigDecimal toBigDecimal(String s) {
		try {
			return NumberUtils.createBigDecimal(s);
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
	}

	public static double toDouble(String s) {
		return NumberUtils.toDouble(s);
	}

	public static boolean isTrue(String s) {
		return StringUtil.phpStrToBool(s);
	}

	public static boolean isNull(String s) {
		return !isTrue(s);
	}

	public static String toString(String s) {
		return s == null ? StringUtils.EMPTY : s;
	}

	public static Object toIntIfNumeric(String s) {
		return (s == null || NumberUtils.isDigits(s)) ? toInt(s) : s;
	}

	public static double roundToDouble(double val, int precision) {
		return roundToBigDecimal(val, precision).doubleValue();
	}

	public static BigDecimal roundToBigDecimal(double val, int precision) {
		return round(new BigDecimal(val), precision);
	}

	public static BigDecimal round(BigDecimal bd, int precision) {
		return bd.setScale(precision, RoundingMode.HALF_UP);
	}

	public static String[] explode(char delimiter, String s) {
		return StringUtils.split(s, delimiter);
	}

	public static String implode(char delimiter, List<String> s) {
		return StringUtils.join(s, delimiter);
	}

	public static String implode(String delimiter, List<String> s) {
		return StringUtils.join(s, delimiter);
	}

	public static boolean is_numeric(String s) {
		return NumberUtils.isCreatable(s);
	}

	public static char[] str_split(String s) {
		if (s == null || s.length() == 0) {
			// PHP version would return false
			return new char[]{};
		}

		return s.toCharArray();
	}


	public static boolean in_array(String needle, String[] haystack) {
		return Stream.of(haystack).anyMatch(s -> s.equals(needle));
	}

	public static String array_shift(List<String> array) {
		if (array.size() == 0) {
			return null;
		}

		return array.remove(0);
	}

	public static BigDecimal max(int i, BigDecimal bd) {
		return new BigDecimal(i).max(bd);
	}

	public static String number_format(BigDecimal number, int decimals) {
		String tmp = FormatUtil.format(number, decimals);
		return tmp.startsWith(PERIOD) ? INT_STR_ZERO + tmp : tmp;
	}
	
	/**
	 * Given player statistics <i>Stolen Bases</i> (<i>sb</i>) and <i>Caught Stealing</i> (<i>cs</i>),<br>
	 * calculate <strong>cs/(sb+cs)</strong> if the sum is not 0.
	 * 
	 * @param stolenBases
	 * @param caughtStealing
	 * @return Caught Stealing Percentage, or zero in <strong>BigDecimal</strong>
	 */
	public static double getCaughtStealingPercentage(BigDecimal stolenBases, BigDecimal caughtStealing) {
		double sb = stolenBases.doubleValue();
		double cs = caughtStealing.doubleValue();
		double sum = sb + cs;
		
		if (sum == 0) {
			return sum;
		} else {
			return (cs / sum) * 100;
		}
	}
}
