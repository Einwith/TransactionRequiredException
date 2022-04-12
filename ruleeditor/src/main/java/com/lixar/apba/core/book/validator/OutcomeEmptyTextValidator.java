package com.lixar.apba.core.book.validator;

import com.lixar.apba.core.book.model.PlayId;
import com.lixar.apba.domain.OutcomeValidationError;
import com.lixar.apba.domain.IBookEntry;
import com.lixar.apba.web.rest.dto.DiceResultConditionDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutcomeEmptyTextValidator extends AbstractOutcomeValidator {
    @Override
    public boolean validate(PlayId id, DiceResultConditionDTO condition, List<OutcomeValidationError> errors) {
        String text = condition.getDisplayText();
        if (StringUtils.isBlank(text)) {
            String errorMessage = "Missing text. Not critical as it will fall back to default text.";

            if (StringUtils.equals(condition.getCsvConditions(), IBookEntry.DEFAULT_CONDITION)) {
                errorMessage = "Missing text for a default entry!";
            }

            errors.add(createValidationError(id, condition, errorMessage));
            return false;
        }

        return true;
    }
}
