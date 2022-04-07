package com.lixar.apba.service.book.converter;

import com.lixar.apba.core.book.model.Action;
import com.lixar.apba.web.rest.util.ParserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ActionFactory {
    public static final String APPLIES_TO_NEXT_TURN = "buff";

    public Action createAction(String actionString) {
        Action action = new Action();

        String[] actionParts = ParserUtil.splitTerm(actionString);

        if (actionParts.length < 1) {
            action.setFirst(StringUtils.EMPTY);
            action.setUnknownAction(true);

            return action;
        }

        int index = 0;
        if (actionParts[index].equals(APPLIES_TO_NEXT_TURN)) {
            action.setBuff(true);
            index++;
        }

        if (actionParts.length - index > 3) {
            action.setFirst(actionString);
            action.setUnknownAction(true);

            return action;
        }

        for (int i = index, actionIndex = 0; i < actionParts.length; i++, actionIndex++) {
            action.setPart(actionIndex, actionParts[i]);
        }

        normalizeAction(action);

        return action;
    }

    private void normalizeAction(Action action) {
        if (action.partEquals(0, "bb") && action.getActivePartCount() == 1) {
            action.setPart(1, "n");
        } else if (action.partEquals(0, "fouls")) {
            action.setPart(0, "foul");
        } else if (action.partEquals(0, "strikes") && action.getActivePartCount() == 1) {
            action.setPart(1, "1");
        } else if (action.partEquals(0, "steal") && action.getActivePartCount() == 2) {
            action.setPart(2, "safe");
        }
    }
}
