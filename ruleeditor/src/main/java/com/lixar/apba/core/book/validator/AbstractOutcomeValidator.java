package com.lixar.apba.core.book.validator;

import com.lixar.apba.core.book.factory.OutcomeValidationErrorFactory;
import com.lixar.apba.core.book.model.PlayId;
import com.lixar.apba.domain.OutcomeValidationError;
import com.lixar.apba.web.rest.dto.DiceResultConditionDTO;

import javax.inject.Inject;

public abstract class AbstractOutcomeValidator implements OutcomeValidator {
    @Inject
    OutcomeValidationErrorFactory outcomeValidationErrorFactory;

    protected OutcomeValidationError createValidationError(PlayId id, DiceResultConditionDTO condition, String errorMessage) {
        return outcomeValidationErrorFactory.createValidationError(id, condition, errorMessage);
    }
}
