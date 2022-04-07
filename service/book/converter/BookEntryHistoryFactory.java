package com.lixar.apba.service.book.converter;

import com.lixar.apba.domain.BookEntryHistory;
import com.lixar.apba.domain.DiceResult;
import com.lixar.apba.domain.IBookEntry;
import com.lixar.apba.repository.DiceResultRepository;
import com.lixar.apba.service.util.DiceParserUtil;
import com.lixar.apba.service.util.LoggerUtil;
import com.lixar.apba.web.rest.dto.DiceResultConditionDTO;
import com.lixar.apba.web.rest.util.ParserUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class BookEntryHistoryFactory {

    private final Logger log = LoggerFactory.getLogger(BookEntryHistoryFactory.class);

    @Inject
    DiceResultRepository diceResultRepository;

    public BookEntryHistory createBookEntryHistory(IBookEntry bookEntry) {
        BookEntryHistory history = new BookEntryHistory();
        // Note: book_entry_history table has a foreign key on column result_id which points to id column of book_entry.
        history.setResultId(bookEntry.getId());
        history.setResultOld(bookEntry.getResultCurrent());
        history.setConditionOld(bookEntry.getParserCondition());

        long timestamp = (new Date()).getTime()/1000L;
        history.setResultTime(timestamp);

        history.setActionOld(getExistingAction(bookEntry));

        return history;
    }

    private String getExistingAction(IBookEntry bookEntry) {
        Set<String> conditionToFind = ParserUtil.splitValues(bookEntry.getParserCondition());

        //search for dice result based on dice_poll (page), dice_sid (section), and die (requirement)
        List<DiceResult> diceResults = diceResultRepository.findAllByDicePoolAndDiceSidAndRequirement(bookEntry.getPage(), bookEntry.getSection(), bookEntry.getResultId());

        for(DiceResult diceResult: diceResults) {
            List<DiceResultConditionDTO> diceConditions = DiceParserUtil.extractDiceResultConditions(diceResult);
            for (DiceResultConditionDTO currentDiceCondition : diceConditions) {
                if (currentDiceCondition.getConditions().equals(conditionToFind)) {
                    return currentDiceCondition.getOutcome();
                }
            }
        }

        log.warn("Unable to find existing actions for " + LoggerUtil.getIdentifier(bookEntry));

        return StringUtils.EMPTY;
    }
}
