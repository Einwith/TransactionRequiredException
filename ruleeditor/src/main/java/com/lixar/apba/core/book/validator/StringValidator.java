package com.lixar.apba.core.book.validator;

import java.util.List;
import java.util.Map;

public interface StringValidator {
    String MIN_KEY = "min";
    String MAX_KEY = "max";
    String SECOND_KEY = "second";

    boolean validate(String str, Map<String, String> context, List<String> errors);
}
