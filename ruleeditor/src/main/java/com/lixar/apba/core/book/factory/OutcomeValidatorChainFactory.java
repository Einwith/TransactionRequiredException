package com.lixar.apba.core.book.factory;

import com.lixar.apba.core.book.validator.OutcomeActionValidator;
import com.lixar.apba.core.book.validator.OutcomeValidator;
import com.lixar.apba.core.book.validator.OutcomeValidatorChain;
import com.lixar.apba.core.book.validator.OutcomeTextBracketValidator;
import com.lixar.apba.core.book.validator.OutcomeTextInsertValidator;
import com.lixar.apba.core.book.validator.OutcomeEmptyTextValidator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class OutcomeValidatorChainFactory {

    @Inject
    OutcomeTextBracketValidator outcomeTextBracketValidator;

    @Inject
    OutcomeTextInsertValidator outcomeTextInsertValidator;

    @Inject
    OutcomeEmptyTextValidator outcomeEmptyTextValidator;

    @Inject
    OutcomeActionValidator actionValidator;

    public OutcomeValidator create() {
        OutcomeValidatorChain validator = new OutcomeValidatorChain();

        validator.addValidator(outcomeTextBracketValidator);
        validator.addValidator(outcomeTextInsertValidator);
        validator.addValidator(outcomeEmptyTextValidator);
        validator.addValidator(actionValidator);

        return validator;
    }
}

