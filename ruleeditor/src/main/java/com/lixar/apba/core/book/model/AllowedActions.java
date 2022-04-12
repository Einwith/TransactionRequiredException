package com.lixar.apba.core.book.model;

import com.lixar.apba.core.book.validator.ActionTextValidator;
import com.lixar.apba.core.book.validator.MaxRunnerValidator;
import com.lixar.apba.core.book.validator.StringValidator;
import com.lixar.apba.core.book.validator.TextRangeValidator;

import java.util.HashMap;
import java.util.Map;

public class AllowedActions {
    public static final Map<String, AllowedAction> allowedActionMap = createAllowedActions();

    private static Map<String, AllowedAction> createAllowedActions() {
        Map<String, AllowedAction> map = new HashMap<>();

        StringValidator maxRunnerValidator = new MaxRunnerValidator();
        Map<String, String> runnerContext = new HashMap<>(); // nothing to specify

        Map<String, String> stealResults = new HashMap<>();
        stealResults.put("safe", "Safe");
        stealResults.put("caught", "Caught");

        addAllowedAction(map, createAllowedAction("steal", "Steal (steal)", "Stealing/caught stealing a base. Automatically moves player 1 base and updates earned run/out and/or cs.",
            ActionPlayers.runnerIdToNameMap, stealResults, true));

        addAllowedAction(map, createAllowedAction("pickedoff", "Picked Off (pickedoff)", "Picked off. No automatic game actions.", ActionPlayers.runnerIdToNameMap));

        addAllowedAction(map, createAllowedAction("so", "Strikeout (so)", "Strike out. Pitcher(pitcher_strikeout) and batter (strikeout) stats changed."));
        addAllowedAction(map, createAllowedAction("wp", "Wild Pitch (wp)", "Wild pitch. Pitcher wp stat changed."));
        addAllowedAction(map, createAllowedAction("pb", "Passed Ball (pb)", "Passed Ball. Catcher pb stat changed."));

        addAllowedAction(map, createAllowedAction("dp", "Double Play (dp)", "Player has been part of a double play. double_play stat changed if less than two outs.", ActionPlayers.fielderIdToNameMap));
        addAllowedAction(map, createAllowedAction("tp", "Triple Play (tp)", "Player has been part of a triple play. triple_play stat changed.", ActionPlayers.fielderIdToNameMap));

        addAllowedAction(map, createAllowedAction("rain", "Rain (rain)", "Game over due to rain. Sets the game to inactive."));

        addAllowedAction(map, createAllowedAction("priority_move", "Priority Move (priority_move)", "Move the batter/runner x bases, without adding stats for hits; occurs before the inning ends in the case of 2 outs. Still counts for earned runs.",
            ActionPlayers.batterAndRunnerIdToNameMap, null, 2, maxRunnerValidator, runnerContext, false));

        addAllowedAction(map, createAllowedAction("unearned_priority", "Unearned Priority Move (unearned_priority)", "Same as priority_move except no earned run calculation. Move the batter/runner x bases, without adding stats for hits; occurs before the inning ends in the case of 2 outs.",
            ActionPlayers.batterAndRunnerIdToNameMap, null, 2, maxRunnerValidator, runnerContext, false));

        addAllowedAction(map, createAllowedAction("move", "Move (move)", "Move the batter/runner x bases, without adding stats for hits. Calculates earned run. If batter, causes an at bat.",
            ActionPlayers.batterAndRunnerIdToNameMap, null, 2, maxRunnerValidator, runnerContext, false));

        addAllowedAction(map, createAllowedAction("unearned_move", "Unearned Move (unearned_move)", "Same as move but no earned run calculation. Move the batter/runner x bases, without adding stats for hits. If batter, causes an at bat.",
            ActionPlayers.batterAndRunnerIdToNameMap, null, 2, maxRunnerValidator, runnerContext, false));

        addAllowedAction(map, createAllowedAction("hbp", "Pitcher hit batter (hbp)", "Pitcher hit batter. Pitcher (hitbatter) and batter (hbp) stats updated. No at bat."));

        Map<String, String> baseOnBallResults = new HashMap<>();
        baseOnBallResults.put("i", "Intentional (i)");
        baseOnBallResults.put("n", "Non-Intentional (n)");

        addAllowedAction(map, createAllowedAction("bb", "Base-on-balls (bb)", "Updates pitcher (pitcher_ibb/pitcher_bb) and batter (ibb/bb) stats. No at bat.", baseOnBallResults));

        addAllowedAction(map, createAllowedAction("sac", "Sacrifice Hit (sac)", "Updates pitcher (pitcher_sh) and batter (sh) stats. No at bat."));

        addAllowedAction(map, createAllowedAction("sacfly", "Sacrifice Fly (sacfly)", "Will update pitcher (pitcher_sf?) and batter (sf?) stats. No at bat."));

        Map<String, String> specialTags = new HashMap<>();
        specialTags.put("atbat", "Force At Bat (atbat)");
        specialTags.put("sac", "Sacrifice Play (sac)");
        specialTags.put("sacfly", "Sacrifice Fly (sacfly)");
        specialTags.put("noatbat", "Not an At Bat (noatbat)");
        specialTags.put("rbi=1", "1 Run Batted In (rbi=1)");
        specialTags.put("rbi=2", "2 Run Batted In (rbi=2)");
        specialTags.put("rbi=3", "3 Run Batted In (rbi=3)");
        specialTags.put("rbi=4", "4 Run Batted In (rbi=4)");
        specialTags.put("gdp", "Ground Double Play (gdp)");
        specialTags.put("rbi=0", "No Run Batted In (rbi=0)");

        addAllowedAction(map, createAllowedAction("tag", "Special Tag (tag)", "Read individual descriptions. rbi's force an rbi of the value indicated. Note: sacfly does nothing", specialTags));

        // technically does not require batter but many were done with it
        addAllowedAction(map, createAllowedAction("single", "Single (single)", "Updates batter (single) and pitcher (pitcher_hits) stats, Moves batter one. Earned run calculation. Causes at bat. Updates game hits.", ActionPlayers.batterIdToNameMap));

        // technically does not require batter but many were done with it
        addAllowedAction(map, createAllowedAction("double", "Double (double)", "Updates batter (double) and pitcher (pitcher_2b) stats, Moves batter two. Earned run calculation. Causes at bat. Updates game hits.", ActionPlayers.batterIdToNameMap));

        // technically does not require batter but many were done with it
        addAllowedAction(map, createAllowedAction("triple", "Triple (triple)", "Updates batter (triple) and pitcher (pitcher_3b) stats, Moves batter three. Earned run calculation. Causes at bat. Updates game hits.", ActionPlayers.batterIdToNameMap));

        // technically does not require batter but many were done with it
        addAllowedAction(map, createAllowedAction("homerun", "Homerun (homerun)", "Updates batter (homerun) and pitcher (pitcher_hits, pitcher_hr) stats, Moves batter four. Earned run calculation. Causes at bat. Updates game hits.", ActionPlayers.batterIdToNameMap));

        addAllowedAction(map, createAllowedAction("o", "Out (o)", "Moves player out. Earned run calculation. If player was batter, causes at bat.", ActionPlayers.batterAndRunnerIdToNameMap));

        addAllowedAction(map, createAllowedAction("ero", "Earned Run Out (ero)", "Out for the purposes of an Earned Run. Earned run out calculation. Updated putout stat for player (is this wrong?)", ActionPlayers.batterAndRunnerIdToNameMap));

        addAllowedAction(map, createAllowedAction("po", "Put Out (po)", "Put out attributed to. Updates player putout stat.", ActionPlayers.fielderIdToNameMap));

        addAllowedAction(map, createAllowedAction("a", "Assist (a)", "Assist given to. Updates player assist stat.", ActionPlayers.fielderIdToNameMap));

        addAllowedAction(map, createAllowedAction("e", "Error by (e)", "Updates player error stat + game error stats.", ActionPlayers.fielderIdToNameMap));

        StringValidator rangeValidator = new TextRangeValidator();
        Map<String, String> ballRangeContext = new HashMap<>();
        ballRangeContext.put(StringValidator.MIN_KEY, "1");
        ballRangeContext.put(StringValidator.MAX_KEY, "4");

        addAllowedAction(map, createAllowedAction("balls", "Balls (balls)", "Add balls to current batter. Moves batter if required (updates pitcher_bb, bb stats + no at bat + earned run calculation). Updates game stat balls.",
            null, null, 1, rangeValidator, ballRangeContext, true));

        addAllowedAction(map, createAllowedAction("foul", "Foul (foul)", "Adds a game strike"));

        Map<String, String> strikerRangeContext = new HashMap<>();
        strikerRangeContext.put(StringValidator.MIN_KEY, "1");
        strikerRangeContext.put(StringValidator.MAX_KEY, "3");

        addAllowedAction(map, createAllowedAction("strikes", "Strike (strikes)", "Adds x game strikes. If over 3, set pitcher_strikeout, strikeout, putout stats, earned run out calculation, moves the player out.",
            null, null, 1, rangeValidator, strikerRangeContext, true));

        addAllowedAction(map, createAllowedAction("ejected", "Ejected (ejected)", "Ejects the player.", ActionPlayers.allIdToNameMap));

        addAllowedAction(map, createAllowedAction("remove", "Remove (remove)", "Removes the player from the game (not ejected or injured)", ActionPlayers.allIdToNameMap));

        Map<String, String> injuryRangeContext = new HashMap<>();
        injuryRangeContext.put(StringValidator.MIN_KEY, "1");
        injuryRangeContext.put(StringValidator.MAX_KEY, "100");

        addAllowedAction(map, createAllowedAction("injury", "Injury (injury)", "Updates injury stat", ActionPlayers.allIdToNameMap, null,
            2, rangeValidator, injuryRangeContext, false));

        addAllowedAction(map, createAllowedAction("delay", "Delay (delay)", "No game effect"));

        addAllowedAction(map, createAllowedAction("balk", "Balk (balk)", "Updates pitcher balk stat"));

        addAllowedAction(map, createAllowedAction("donothing", "Do nothing (donothing)", "No game effect"));

        StringValidator textValidator = new ActionTextValidator();

        addAllowedAction(map, createAllowedAction("text", "Text (text)", "Changes output text - UNSAFE.",
            null, null, 1, textValidator, null, false));

        return map;
    }

    private static void addAllowedAction(Map<String, AllowedAction> map, AllowedAction action) {
        map.put(action.getValue(), action);
    }

    private static AllowedAction createAllowedAction(String key, String text, String description) {
        return createAllowedAction(key, text, description, null, null, AllowedAction.INVALID_TEXT_INPUT_INDEX, null, null, false);
    }

    private static AllowedAction createAllowedAction(String key, String text, String description, Map<String, String> allowedSecondActions) {
        return createAllowedAction(key, text, description, allowedSecondActions, null, AllowedAction.INVALID_TEXT_INPUT_INDEX,
            null, null, false);
    }

    private static AllowedAction createAllowedAction(String key, String text, String description,
                                                     Map<String, String> allowedSecondActions,
                                                     Map<String, String> allowedThirdActions,
                                                     boolean canBuff) {
        return createAllowedAction(key, text, description, allowedSecondActions, allowedThirdActions, AllowedAction.INVALID_TEXT_INPUT_INDEX,
            null, null, canBuff);
    }

    private static AllowedAction createAllowedAction(String key, String text, String description,
                                    Map<String, String> allowedSecondActions,
                                    Map<String, String> allowedThirdActions,
                                    int textInputIndex,
                                    StringValidator validator,
                                    Map<String, String> validationContext,
                                    boolean canBuff) {
        AllowedAction action = new AllowedAction();

        action.setValue(key);
        action.setText(text);
        action.setDescription(description);
        action.setAllowedSecondEntries(allowedSecondActions);
        action.setAllowedThirdEntries(allowedThirdActions);
        action.setTextInputIndex(textInputIndex);
        action.setInputValidator(validator);
        action.setValidationContext(validationContext);
        action.setCanBuff(canBuff);

        return action;
    }


}
