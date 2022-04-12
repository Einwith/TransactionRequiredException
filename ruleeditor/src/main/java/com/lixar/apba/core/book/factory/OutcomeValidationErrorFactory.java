package com.lixar.apba.core.book.factory;

import com.lixar.apba.core.book.model.PlayId;
import com.lixar.apba.domain.OutcomeValidationError;
import com.lixar.apba.web.rest.dto.DiceResultConditionDTO;
import org.springframework.stereotype.Service;

@Service
public class OutcomeValidationErrorFactory {
    public OutcomeValidationError createValidationError(PlayId id, DiceResultConditionDTO condition, String errorMessage) {
        OutcomeValidationError error = new OutcomeValidationError();
        error.setDicePool(id.getDicePool());
        error.setDiceSid(id.getDiceSid());
        error.setCsvConditions(condition.getCsvConditions());
        error.setRequirement(id.getRequirement());
        error.setErrorMessage(errorMessage);

        return error;
    }
}
