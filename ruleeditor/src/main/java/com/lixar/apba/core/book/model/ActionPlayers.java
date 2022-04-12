package com.lixar.apba.core.book.model;

import java.util.HashMap;
import java.util.Map;

public class ActionPlayers {

    public static final String RUNNER_ON_FIRST_ID = "r1";
    public static final String RUNNER_ON_SECOND_ID = "r2";
    public static final String RUNNER_ON_THIRD_ID = "r3";
    public static final String BATTER_ID = "bt";
    public static final String CATCHER_ID = "c";
    public static final String PITCHER_ID = "p";
    public static final String SHORTSTOP_ID = "ss";
    public static final String FIRST_BASE_ID = "1b";
    public static final String SECOND_BASE_ID = "2b";
    public static final String THIRD_BASE_ID = "3b";
    public static final String LEFT_FIELD_ID = "lf";
    public static final String CENTER_FIELD_ID = "cf";
    public static final String RIGHT_FIELD_ID = "rf";

    public static final Map<String, String> allIdToNameMap = createIdToNameMap();

    private static final String[] fielderIds = new String[] {CATCHER_ID, PITCHER_ID, SHORTSTOP_ID, FIRST_BASE_ID, SECOND_BASE_ID, THIRD_BASE_ID, LEFT_FIELD_ID, CENTER_FIELD_ID, RIGHT_FIELD_ID};

    public static final Map<String, String> runnerIdToNameMap = createGenericIdToNameMap(new String[] {RUNNER_ON_FIRST_ID, RUNNER_ON_SECOND_ID, RUNNER_ON_THIRD_ID});
    public static final Map<String, String> batterIdToNameMap = createGenericIdToNameMap(new String[] {BATTER_ID});
    public static final Map<String, String> batterAndRunnerIdToNameMap = createGenericIdToNameMap(new String[] {BATTER_ID, RUNNER_ON_FIRST_ID, RUNNER_ON_SECOND_ID, RUNNER_ON_THIRD_ID});
    public static final Map<String, String> fielderIdToNameMap = createGenericIdToNameMap(fielderIds);


    private static Map<String, String> createIdToNameMap() {
        Map<String, String> map = new HashMap<>();

        map.put(CATCHER_ID, "Catcher");
        map.put(BATTER_ID, "Batter");
        map.put(PITCHER_ID, "Pitcher");
        map.put(SHORTSTOP_ID, "Shortstop");
        map.put(FIRST_BASE_ID, "First");
        map.put(SECOND_BASE_ID, "Second");
        map.put(THIRD_BASE_ID, "Third");
        map.put(LEFT_FIELD_ID, "Left");
        map.put(CENTER_FIELD_ID, "Center");
        map.put(RIGHT_FIELD_ID, "Right");
        map.put(RUNNER_ON_FIRST_ID, "Runner on First");
        map.put(RUNNER_ON_SECOND_ID, "Runner on Second");
        map.put(RUNNER_ON_THIRD_ID, "Runner on Third");

        return map;
    }

    private static Map<String, String> createGenericIdToNameMap(String[] keys) {
        Map<String, String> map = new HashMap<>();

        for (String key : keys) {
            addToMap(key, map);
        }

        return map;
    }

    private static void addToMap(String key, Map<String, String> map) {
        map.put(key, allIdToNameMap.get(key));
    }
}
