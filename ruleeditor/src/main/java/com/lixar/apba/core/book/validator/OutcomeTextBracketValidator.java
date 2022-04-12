package com.lixar.apba.core.book.validator;

import com.lixar.apba.core.book.model.PlayId;
import com.lixar.apba.domain.OutcomeValidationError;
import com.lixar.apba.web.rest.dto.DiceResultConditionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutcomeTextBracketValidator extends AbstractOutcomeValidator {
    @Override
    public boolean validate(PlayId id, DiceResultConditionDTO condition, List<OutcomeValidationError> errors) {
        int openBrackets = 0;
        int closedBrackets = 0;

        String text = condition.getDisplayText();
        for ( int i = 0; i < text.length(); i++ ) {
            char ch = text.charAt(i);

            if (ch == '[') {
                openBrackets++;

                if (openBrackets > closedBrackets + 1) {
                    errors.add(createValidationError(id, condition, "Did not properly close a square bracket before opening another."));
                    return false;
                }
            } else if (ch == ']') {
                closedBrackets++;

                if (closedBrackets > openBrackets) {
                    errors.add(createValidationError(id, condition, "Did not properly open a square bracket before closing one."));
                    return false;
                }
            }
        }

        if (openBrackets != closedBrackets) {
            errors.add(createValidationError(id, condition, "Did not properly close all square brackets."));
            return false;
        }

        return true;
    }
}
