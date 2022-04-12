package com.lixar.apba.core.book.validator;

import com.lixar.apba.core.book.model.Action;
import com.lixar.apba.core.book.model.ActionPlayers;
import com.lixar.apba.core.book.model.AllowedAction;
import com.lixar.apba.core.book.model.AllowedActions;
import com.lixar.apba.core.book.model.PlayId;
import com.lixar.apba.domain.OutcomeValidationError;
import com.lixar.apba.service.book.converter.ActionFactory;
import com.lixar.apba.service.util.DiceCategories;
import com.lixar.apba.web.rest.dto.DiceResultConditionDTO;
import com.lixar.apba.web.rest.util.ParserUtil;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OutcomeActionValidator extends AbstractOutcomeValidator {
    @Inject
    ActionFactory actionFactory;

    private static final Map<Integer, Set<String>> dicePoolToInvalidRunnersMap = createDicePoolToInvalidRunnersMap();

    @Override
    public boolean validate(PlayId id, DiceResultConditionDTO condition, List<OutcomeValidationError> errors) {
        String csvActions = condition.getOutcome();
        Set<String> actionStrings = ParserUtil.splitValues(csvActions);

        for (String actionString : actionStrings) {
            Action action = actionFactory.createAction(actionString);

            if (!validateAction(id, condition, actionString, action, errors)) {
                return false;
            }
        }

        return true;
    }

    private boolean validateAction(PlayId id, DiceResultConditionDTO condition, String actionString, Action action, List<OutcomeValidationError> errors) {
        if (action.isUnknownAction() || action.getFirst() == null) {
            errors.add(createValidationError(id, condition, "Unable to determine the action for: " + actionString));
            return false;
        }

        AllowedAction rule = AllowedActions.allowedActionMap.get(action.getFirst());

        if (rule == null) {
            errors.add(createValidationError(id, condition, "Invalid first term in action: " + actionString));
            return false;
        }

        //noinspection SimplifiableIfStatement
        if (!validateInputTypes(id, condition, rule, actionString, action, errors)) {
            return false;
        }

        if (!validateProperRunners(id, condition, rule, actionString, action, errors)) {
            return false;
        }

        return validateSpecializedInput(id, condition, rule, actionString, action, errors);
    }

    private boolean validateProperRunners(PlayId id, DiceResultConditionDTO condition, AllowedAction rule, String actionString, Action action, List<OutcomeValidationError> errors) {
        Set<String> invalidRunners = dicePoolToInvalidRunnersMap.get(id.getDicePool());

        // we don't check buffs that apply in the future (for now)
        if (invalidRunners == null || action.isBuff()) {
            return true;
        }

        // This is a simplification - technically we should only check the field that has a runner look up
        for (String part : action.getParts()) {
            if (part != null && invalidRunners.contains(part)) {
                errors.add(createValidationError(id, condition, "Invalid runner for the current base loading - might need a buff?: " + actionString));
                return false;
            }
        }

        return true;
    }



    private boolean validateInputTypes(PlayId id, DiceResultConditionDTO condition, AllowedAction rule, String actionString, Action action, List<OutcomeValidationError> errors) {
        if (rule.getAllowedSecondEntries() == null && action.getActivePartCount() > 1 && rule.getTextInputIndex() != 1) {
            errors.add(createValidationError(id, condition, "Invalid second term in action - there should not be a second term: " + actionString));
            return false;
        }

        if (rule.getAllowedThirdEntries() == null && action.getActivePartCount() > 2 && rule.getTextInputIndex() != 2) {
            errors.add(createValidationError(id, condition, "Invalid third term in action - there should not be a third term: " + actionString));
            return false;
        }

        if (rule.getAllowedSecondEntries() != null && !rule.getAllowedSecondEntries().containsKey(action.getSecond())) {
            errors.add(createValidationError(id, condition, "Invalid choice for the second term: " + actionString));
            return false;

        }

        if (rule.getAllowedThirdEntries() != null && !rule.getAllowedThirdEntries().containsKey(action.getThird())) {
            errors.add(createValidationError(id, condition, "Invalid choice for the third term: " + actionString));
            return false;
        }

        if (!rule.isCanBuff() && action.isBuff()) {
            errors.add(createValidationError(id, condition, "Action has buff but is not allowed one: " + actionString));
            return false;
        }

        return true;
    }

    private boolean validateSpecializedInput(PlayId id, DiceResultConditionDTO condition, AllowedAction rule, String actionString, Action action, List<OutcomeValidationError> errors) {
        StringValidator validator = rule.getInputValidator();
        if (validator == null) {
            return true;
        }

        Map<String, String> context = new HashMap<>(rule.getValidationContext());
        context.put(StringValidator.SECOND_KEY, action.getSecond());

        List<String> textErrors = new ArrayList<>();
        if (!validator.validate(action.getPart(rule.getTextInputIndex()), context, textErrors)) {
            errors.addAll(textErrors.stream().map(textError -> createValidationError(id, condition, "Error in text of \"" + actionString + "\": " + textError)).collect(Collectors.toList()));
        }

        return true;
    }

    private static Map<Integer, Set<String>> createDicePoolToInvalidRunnersMap() {
        Map<Integer, Set<String>> map = new HashMap<>();

        map.put(DiceCategories.BASES_EMPTY_ID.getId(), Stream.of(ActionPlayers.RUNNER_ON_FIRST_ID, ActionPlayers.RUNNER_ON_SECOND_ID, ActionPlayers.RUNNER_ON_THIRD_ID).collect(Collectors.toSet()));
        map.put(DiceCategories.RUNNER_ON_FIRST_ID.getId(), Stream.of(ActionPlayers.RUNNER_ON_SECOND_ID, ActionPlayers.RUNNER_ON_THIRD_ID).collect(Collectors.toSet()));
        map.put(DiceCategories.RUNNER_ON_SECOND_ID.getId(), Stream.of(ActionPlayers.RUNNER_ON_FIRST_ID, ActionPlayers.RUNNER_ON_THIRD_ID).collect(Collectors.toSet()));
        map.put(DiceCategories.RUNNER_ON_THIRD_ID.getId(), Stream.of(ActionPlayers.RUNNER_ON_FIRST_ID, ActionPlayers.RUNNER_ON_SECOND_ID).collect(Collectors.toSet()));
        map.put(DiceCategories.RUNNER_ON_FIRST_AND_SECOND_ID.getId(), Stream.of(ActionPlayers.RUNNER_ON_THIRD_ID).collect(Collectors.toSet()));
        map.put(DiceCategories.RUNNER_ON_FIRST_AND_THIRD_ID.getId(), Stream.of(ActionPlayers.RUNNER_ON_SECOND_ID).collect(Collectors.toSet()));
        map.put(DiceCategories.RUNNER_ON_SECOND_AND_THIRD_ID.getId(), Stream.of(ActionPlayers.RUNNER_ON_FIRST_ID).collect(Collectors.toSet()));

        return map;
    }
}
