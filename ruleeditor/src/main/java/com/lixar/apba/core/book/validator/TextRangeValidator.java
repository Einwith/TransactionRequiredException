package com.lixar.apba.core.book.validator;

import java.util.List;
import java.util.Map;

public class TextRangeValidator implements StringValidator {
    @Override
    public boolean validate(String str, Map<String, String> context, List<String> errors) {
        int min = Integer.parseInt(context.get(MIN_KEY));
        int max = Integer.parseInt(context.get(MAX_KEY));

        if (str == null || !str.matches("^\\d+$")) {
            errors.add("Not a number: " + str);
            return false;
        }

        int current = Integer.parseInt(str);

        if (current > max || current < min) {
            errors.add("Value not within allowed range of min: " + min + " and max: " + max);
            return false;
        }

        return true;
    }
}
