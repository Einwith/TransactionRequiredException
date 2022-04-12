package com.lixar.apba.core.book.validator;

import com.lixar.apba.core.book.model.ActionPlayers;
import java.util.List;
import java.util.Map;

public class MaxRunnerValidator implements StringValidator {
    @Override
    public boolean validate(String str, Map<String, String> context, List<String> errors) {
        String player = context.get(StringValidator.SECOND_KEY);

        int runner = player.equals(ActionPlayers.BATTER_ID) ? 0 : Integer.parseInt(String.valueOf(player.charAt(1)));

        int maxMovement = 4 - runner; // 4 bases - base runner is at currently

        if (str == null || !str.matches("^\\d$")) {
            errors.add("Not a single-digit number: " + str);
            return false;
        }

        if (Integer.parseInt(str) > maxMovement) {
            errors.add("Too many bases - maximum for this runner should be: " + maxMovement);
            return false;
        }

        return true;
    }
}
