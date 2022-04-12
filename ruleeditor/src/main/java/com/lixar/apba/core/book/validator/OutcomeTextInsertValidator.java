package com.lixar.apba.core.book.validator;

import com.lixar.apba.core.book.model.PlayId;
import com.lixar.apba.domain.OutcomeValidationError;
import com.lixar.apba.web.rest.dto.DiceResultConditionDTO;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OutcomeTextInsertValidator extends AbstractOutcomeValidator {
    private static final Set<String> TEXT_INSERTS = new HashSet<>(Arrays.asList(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "b",
        "r1",
        "r2",
        "r3"
    ));

    private static final Pattern INSERT_PATTERN = Pattern.compile("\\[([^\\]]+)\\]");

    @Override
    public boolean validate(PlayId id, DiceResultConditionDTO condition, List<OutcomeValidationError> errors) {
        Matcher m = INSERT_PATTERN.matcher(condition.getDisplayText());
        while (m.find()) {
            String found = m.group(1);
            if (!TEXT_INSERTS.contains(found)) {
                errors.add(createValidationError(id, condition, "Invalid text insert: [" + found + "]"));
                return false;
            }
        }

        return true;
    }
}
