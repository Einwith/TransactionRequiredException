package com.lixar.apba.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static com.lixar.apba.core.util.GameConstants.*;

import org.thymeleaf.util.StringUtils;

public class FormatUtil {

	// From GameService
	public String format(BigDecimal initial) {
		return format(initial, 3);
	}

	// From GameService
	public static String format(BigDecimal initial, int decimal) {
		BigDecimal mult = new BigDecimal(10).pow(decimal);

		if (initial.compareTo(BigDecimal.ONE) >= 0) {
			DecimalFormat df = new DecimalFormat(FORMAT_THOUSANDS + StringUtils.repeat(INT_STR_ZERO, decimal));
			return df.format(initial);
		} else if (initial.compareTo(BigDecimal.ZERO) == 0) {
			DecimalFormat df = new DecimalFormat(PERIOD + StringUtils.repeat(INT_STR_ZERO, decimal));
			return df.format(initial);
		} else {
			BigDecimal result = initial.multiply(mult).setScale(0, RoundingMode.FLOOR);
			return PERIOD + new DecimalFormat(FORMAT_SINGLE_CHARACTER).format(result);
		}
	}
}
