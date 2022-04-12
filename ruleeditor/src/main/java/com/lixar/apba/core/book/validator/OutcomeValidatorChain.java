package com.lixar.apba.core.book.validator;


import com.lixar.apba.core.book.model.PlayId;
import com.lixar.apba.domain.OutcomeValidationError;
import com.lixar.apba.web.rest.dto.DiceResultConditionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OutcomeValidatorChain extends AbstractOutcomeValidator {
    private final Logger log = LoggerFactory.getLogger(OutcomeValidatorChain.class);

    private List<OutcomeValidator> validators = new ArrayList<>();

    public void addValidator(OutcomeValidator validator) {
        validators.add(validator);
    }

    @Override
    public boolean validate(PlayId id, DiceResultConditionDTO condition, List<OutcomeValidationError> errors) {
        boolean invalidConditionFound = false;

        try {
            for (OutcomeValidator validator : validators) {
                invalidConditionFound |= validator.validate(id, condition, errors);
            }
        } catch (Exception e) {
            log.error("Error validating entry", e);
            errors.add(createValidationError(id, condition, "Error trying to validate"));
            return false;
        }

        return invalidConditionFound;
    }
}
