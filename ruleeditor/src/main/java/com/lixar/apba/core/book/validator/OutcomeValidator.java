package com.lixar.apba.core.book.validator;

import com.lixar.apba.core.book.model.PlayId;
import com.lixar.apba.domain.OutcomeValidationError;
import com.lixar.apba.web.rest.dto.DiceResultConditionDTO;

import java.util.List;

public interface OutcomeValidator {
    boolean validate(PlayId id, DiceResultConditionDTO condition, List<OutcomeValidationError> errors);
}
