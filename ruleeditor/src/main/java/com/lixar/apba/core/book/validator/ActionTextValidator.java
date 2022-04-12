package com.lixar.apba.core.book.validator;

import java.util.List;
import java.util.Map;

public class ActionTextValidator implements StringValidator {

    @Override
    public boolean validate(String str, Map<String, String> context, List<String> errors) {
        if (str.indexOf(',') > -1) {
            errors.add("Can't have a comma in the text.");
            return false;
        }

        if (str.indexOf(';') > -1) {
            errors.add("Can't have a semi-colon in the text.");
            return false;
        }

        if (str.indexOf('@') > -1) {
            errors.add("Can't have an at (@) sign in the text.");
            return false;
        }

        if (str.indexOf('-') > -1) {
            errors.add("Can't have a dash in the text.");
            return false;
        }

        return true;
    }
}
