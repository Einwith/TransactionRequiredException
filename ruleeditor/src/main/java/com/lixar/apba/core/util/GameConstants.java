package com.lixar.apba.core.util;

import static com.lixar.apba.web.ModelConstants.DEFAULT_CONTEXT;

public class GameConstants {

	public static final String 
			GET_REQUEST = "GET",
			POST_REQUEST = "POST", 
			SUCCESS_RESPONSE = "success",
			
			LIXAR_ID = "lixar_id";
	
	// CREATE GAME ADJUSTMENTS

	public static final String 
			AWAY = "away",
			AWAY_LABEL = "Away",
			AWAY_NAME = "away_name",
			AWAY_ABBR = "away_abbr",
			AWAY_TEAM_YEAR = "away_team_year",
			HOME = "home",
			HOME_LABEL = "Home",
			HOME_NAME = "home_name",
			HOME_ABBR = "home_abbr",
			HOME_TEAM_YEAR = "home_team_year",
			
			ABBREVIATION_LABEL = "abbreviation",
			AI_CAP_LABEL = "AI",
			AI_UNCAP_LABEL = "ai",
			APBA_GAME_BASIC_LABEL = "APBA Basic Game",
			APBA_PLAYER_LABEL = "apba_player",
			AVATAR_LABEL = "avatar",
			AWAY_GUID_LABEL = "awayGUID",
			AWAY_TEAM_LABEL = "awayteam",
			AWAY_TEAM_TITLE_LABEL = "awayTeam",
			BASIC_DH = "basicDH",
			BASIC_NON_DH = "basicNonDH",
			BATTING_LABEL = "batting",
			BATTING_ORDER_INDEX = "battingOrderIndex",
			BATTING_ORDER_LABEL = "battingOrder",
			BOXSCORE_LABEL = "boxscore",
			CHECKED_BOOLEAN_LABEL = "checked",
			CURRENT_INNING = "currentInning",
			CUSTOM_LABEL = "Custom",
			DATA_LABEL = "data",
			DEFAULTS_LABEL = "defaults",
			DEFENSIVE_POSITION = "defensivePosition",
			DESIGNATED_HITTER_AWAY_LABEL = "Designated Hitter Away",
			DESIGNATED_HITTER_HOME_LABEL = "Designated Hitter Home",
			DISPLAY_NAME_LABEL = "displayName",
			ELIGIBLE_LABEL = "ELIGIBLE",
			EXHIBITION_LABEL = "exhibition",
			GAME_END_LABEL_ABORT = "Abort",
			GAME_END_LABEL_RAIN = "Rain",
			GAME_UNCAP_LABEL = "game",
			H_AVG_LABEL = "hAVG",
			HOME_GUID_LABEL = "homeGUID",
			HOME_TEAM_LABEL = "hometeam",
			HOME_TEAM_TITLE_LABEL = "homeTeam",
			ID_LABEL = "id",
			IW_WITH_BASES_FULL = "IW With Bases Full",
			LEAGUE_LABEL = "league",
			LINEUP_BENCHED_LABEL = "benched",
			LINEUPS_LABEL = "lineups",
			LINEUP_NAME = "lineup-name",
			LINEUP_NAME_LABEL = "lineupName",
			LINEUP_PLAYER_STATUSES = "playerStatuses",
			LINEUP_TYPE_LABEL = "lineupType",
			NAME_LABEL = "name",
			NPC_LABEL = "npc",
			NPC_START_KEY = "-777",
			OBP_LABEL = "OBP",
			OPPONENT_LABEL = "opponent",
			OPPONENT_ID_LABEL = "opponentid",
			OUTS_LABEL = "outs",
			PAST_POSITION = "past_position",
			PAST_POSITION_LABEL = "past_position",
			PITCHER_LIST_LABEL = "pitcher_list",
			PITCHER_ROTATION_LABEL = "pitcherRotation",
			PLAYER_LABEL = "player",
			PLAYER_ID_LABEL = "playerId",
			PLAYERID_LABEL = "playerid",
			PLAYERS_LABEL = "players",
			POS_LABEL = "pos",
			POSITION_LABEL = "position",
			POSITION_LABEL_SHORT = "pos",
			RUNNER_LABEL = "runner",
			RUNNER_LABEL_SHORT = "R",
			SNAME_LABEL = "sname",
			WAIT_LABEL = "wait",
			
			PITCHER_RATING_SHORT = "pr",
			CATCHER_RATING_SHORT = "cr",
			FIRST_BASE_RATING_SHORT = "1br",
			SECOND_BASE_RATING_SHORT = "2br",
			THIRD_BASE_RATING_SHORT = "3br",
			SHORTSTOP_RATING_SHORT = "ssr",
			OUTFIELD_RATING_SHORT = "ofr",
			
			CATCHER_DEF_RATING = "5",
			FIRST_BASE_DEF_RATING = "1",
			SECOND_BASE_DEF_RATING = "5",
			THIRD_BASE_DEF_RATING = "2",
			SHORTSTOP_DEF_RATING = "5",
			OUTFIELD_DEF_RATING = "0",
			PITCHER_RATING = "0",
			DESIGNATED_HITTER_DEF_RATING = "0",
			
			RATING_LABEL = "rating",
			FATIGUE_LABEL = "FATIGUE",
			RULE_NAME_LABEL = "ruleName",
			RULES_LABEL = "rules",
			SACRIFICE_WITH_2_OUTS = "Sacrifice with 2 Outs",
			SLG_LABEL = "SLG",
			SUB_LABEL = "sub",
			TOURNAMENT_GAME = "tournamentId",
			UN_ASSIGNED_LABEL = "Unassigned",
			UPDATES_LABEL = "updates",
			USER_LABEL = "user",

			ABORT_URL_ = "abort_url",
			CARD_URL_ = "card_url",
			CHAT_URL = "chatUrl",
			COMMUNITY_URL = "communityUrl",
			DETAIL_URL = "detail_url",
			FIREBASE_CHAT_PATH = "firebaseChatPath",
			FORFEIT_URL = "forfeit_url",
			FORM_URL = "form_url",
			GAME_INNING_URL = "gameInningUrl",
			LOG_URL = "log_url",
			OPPOSING_PITCHER_URL = "opposing_pitcher_url",
			READY_URL = "ready_url",
			RESUME_URL = "resume_url",
			SAVE_COLLECTIONS_URL = "save_collections_url",
			SUB_AVAILABLE_URL = "sub_available_url",
			SUB_URL = "sub_url",
			UNITY_CONFIG_URL = "unity_config_url",
			WINDOW_URL_ = "window_url",
			
			LIXAR = "lixar",
	
			ERROR_JSON = "{ \"error_msg\": \"db\" }";
	
	// RULES LABELS
	public static final String 
			RULE_ID_DISABLE_INJURY = "injury_dice",
			RULE_ID_HITTER_AWAY = "hitterAway",
			RULE_ID_HITTER_HOME = "hitterHome",
			RULE_ID_IW = "iw",
			RULE_ID_MANUAL_DICE = "manual_dice",
			RULE_ID_MICRO_MANAGER_LINEUP = "microManagerLineup",
			RULE_ID_OPTIONAL_PITCHING = "optionalPitching",
			RULE_ID_REROLL_RAIN = "rain_dice",
			RULE_ID_SACRIFICE = "sacrifice",
			RULE_ID_STEP_PLAY = "step_play",
			RULE_ID_THREED_DICE = "threed_dice",
			RULE_ID_3D_DICE = "3d_dice",
			RULE_NAME_DISABLE_INJURY = "Injury Dice",
			RULE_NAME_MANUAL_DICE = "Manual Dice Roll",
			RULE_NAME_MICRO_MANAGER_LINEUP = "Micro Manager Lineup",
			RULE_NAME_OPTIONAL_PITCHING = "Optional Pitching Rule",
			RULE_NAME_REROLL_RAIN = "Rain Dice",
			RULE_NAME_STEP_PLAY = "Step Play",
			RULE_NAME_THREED_DICE = "3D Dice",
			
			AUTOPLAY_GAME_LABEL = "autoplayGame",

			DUG_OUT_VISITOR = "dugoutvisitor",
			DUG_OUT_HOME = "dugouthome";
	
	// NPC STAT LABELS
	public static final String 
			AT_BATS = "at_bats",
			BATTER_LABEL = "batter",
			BALKS_LABEL = "balks",
			CATCHER_LABEL = "catcher",
			CENTER_LABEL = "center",
			EARNED_RUNS = "earned_runs",
			FIRST_LABEL = "first",
			GAMES_COMPLETED = "games_completed",
			GAMES_LABEL = "games",
			GAMES_LOST = "games_lost",
			GAMES_SAVED = "games_saved",
			GAMES_STARTED = "games_started",
			GAMES_WON = "games_won",
			H_BASES = "Hiscsastbases",
			H_CAUGHT_STEALING = "Hisbscaughtstealing",
			HIT_BATTERS = "hit_batters",
			HITTER_LABEL = "hitter",
			HOMERUNS = "homeruns",
			HOME_RUNS = "home_runs",
			INFIELD_LABEL = "infield",
			INNINGS_LABEL = "innings",
			INTENTIONAL_WALKS = "intentional_walks",
			LEFT_LABEL = "left",
			NEW_BATTER_LABEL = "new_batter",
			OF_LABEL = "of",
			ONDECK_LABEL = "ondeck",
			PITCHER_LABEL = "pitcher",
			PLATE_APPEARANCES = "plate_appearances",
			RIGHT_LABEL = "right",
			RUNS_CREATED = "Runscreated",
			RUNS_PER_GAME = "runscreatedpergame",
			SAC_FLIES = "sacrifice_flies",
			SACRIFICES = "sacrifices",
			SECOND_LABEL = "second",
			SHORT_LABEL = "short",
			SHORTSTOP_LABEL = "shortstop",
			SLUG_LABEL = "slug",
			SSN_LABEL = "ssn",
			STOLEN_BASE_PERCENT = "stolenbasepercent",
			STRIKEOUTS = "strikeouts",
			TA_LABEL = "ta",
			THIRD_LABEL = "third",
			TOTAL_BASES = "totalbases",
			BATTER_TIMES_HIT = "times_hit",
			WILD_PITCHES = "wild_pitches",
			WIN_PERCENTAGE = "winpercent",
			
			AVERAGE_RUNNER = "Average Runner",
			FAST_RUNNER_LABEL = "F",
			FAST_RUNNER = "Fast Runner",
			SLOW_RUNNER_LABEL = "S",
			SLOW_RUNNER = "Slow Runner",
			
			DEFENSE_LEGEND = "defense_legend",
			OFFENSE_LEGEND = "offense_legend",
			PITCHING_LEGEND = "pitching_legend",
		
			INJURY = "injury",
			
			BATS_LABEL_SHORT = "B",
			GRADE_LABEL_SHORT = "GR",
			GRADE_LABEL_LONG = "grade",
			RGRADE_LABEL = "rgrade",
			SP_LABEL = "SP",
			THROWS_LABEL_SHORT = "T";
	
	// REPO LABELS 
	public static final String
			CD_AGE = "cd_age",
			CD_ARM = "cd_arm",
			CD_BATS = "cd_bats",
			CD_BK = "cd_bk",
			CD_BORN = "cd_born",
			CD_CARD_RATING = "cd_card_rating",
			CD_CONTROL = "cd_control",
			CD_EPOS = "cd_epos",
			CD_FATIGUE = "cd_fatigue",
			CD_FATIGUE_LEVEL = "cd_fatigue_level",
			CD_HB = "cd_hb",
			CD_HEIGHT = "cd_height",
			CD_HITS = "cd_hits",
			CD_HR = "cd_hr",
			CD_HRA = "cd_hrallowed",
			CD_J = "cd_j",
			CD_MF = "cd_mf",
			CD_PB = "cd_pb",
			CD_PLATOON_R = "cd_platoon_r",
			CD_POS = "cd_pos",
			CD_POSR = "cd_posr",
			CD_RATING = "cd_rating",
			CD_SPEED = "cd_speed",
			CD_SR = "cd_sr",
			CD_SSN = "cd_ssn",
			CD_ST = "cd_st",
			CD_STRIKEOUT = "cd_strikeout",
			CD_TH = "cd_th",
			CD_THROWS = "cd_throws",
			CD_WEIGHT = "cd_weight",
			CD_WP = "cd_wp",
			
			CD_TH_LABEL = "th",
			
			S_BA = "s_ba",
			S_HR = "s_hr",
			S_OBP = "s_obp",
			S_SLG = "s_slg",
			S_YR = "s_yr",
			
			SB_2B = "sb_2b",
			SB_3B = "sb_3b",
			SB_AB = "sb_ab",
			SB_ASB = "sb_asb",
			SB_AVG = "sb_avg",
			SB_BB = "sb_bb",
			SB_CS = "sb_cs",
			SB_G = "sb_g",
			SB_GDP = "sb_gdp",
			SB_H = "sb_h",
			SB_HBP = "sb_hbp",
			SB_HR = "sb_hr",
			SB_IBB = "sb_ibb",
			SB_ISO = "sb_iso",
			SB_OBP = "sb_obp",
			SB_OPS = "sb_ops",
			SB_PA = "sb_pa",
			SB_R = "sb_r",
			SB_RC = "sb_rc",
			SB_RCG = "sb_rcg",
			SB_RBI = "sb_rbi",
			SB_SB = "sb_sb",
			SB_SBP = "sb_sbp",
			SB_SECA = "sb_seca",
			SB_SF = "sb_sf",
			SB_SH = "sb_sh",
			SB_SLG = "sb_slg",
			SB_SO = "sb_so",
			SB_TA = "sb_ta",
			SB_TB = "sb_tb",
			SB_WP = "sb_wp",
			
			SP_BB = "sp_bb",
			SP_BK = "sp_bk",
			SP_CG = "sp_cg",
			SP_ER = "sp_er",
			SP_ERA = "sp_era",
			SP_G = "sp_g",
			SP_GS = "sp_gs",
			SP_H = "sp_h",
			SP_HB = "sp_hb",
			SP_HR = "sp_hr",
			SP_IBB = "sp_ibb",
			SP_IP = "sp_ip",
			SP_L = "sp_l",
			SP_PCT = "sp_pct",
			SP_R = "sp_r",
			SP_SO = "sp_so",
			SP_SV = "sp_sv",
			SP_W = "sp_w",
			SP_WP = "sp_wp";
	
	// AVATAR STAT LABELS
	public static final String 
			AT_BAT = "at_bat",
			ATBAT = "atbat",
			ARM_LABEL = "arm",
			ASB_LABEL = "asb",
			ASSIST_LABEL = "assist",
			AVG_LABEL = "avg",
	        BALK_LONG = "balk",
	        BATS_LABEL = "bats",
	        BB_LABEL = "bb",
	        BALK_SHORT = "BK",
	        BATTER_WP = "batterWP",
	        BATTER_GAME = "batter_game",
	        BATTER_STAT = "batter_stat",
	        BATTING_AVERAGE = "battingAverage",
	        BENCHED = "BENCHED",
	        BF_LABEL = "bf",
	        BORN_LABEL = "born",
	        C_LABEL= "c",
	        CARD_RATING_LABEL = "card_rating",
	        CAUGHT_STEALING = "caughtStealing",
	    	CAUGHT_STEALING_SHORT = "cs",
	        CONFIRM_LABEL = "confirm",
	        CONTROL_LABEL = "control",
	        CONTROL_RATING = "CR",
	        CTRL_LABEL = "CTRL",
	        D_LABEL = "d",
	        DEF_LABEL = "def",
	        DEFENSE_TOTAL = "defense_total",
	        DEFENSE_P = "defense_p",
	        DEFENSE_C = "defense_c",
	        DEFENSE_1B = "defense_1b",
	        DEFENSE_2B = "defense_2b",
	        DEFENSE_3B = "defense_3b",
	        DEFENSE_SS = "defense_ss",
	        DEFENSE_OF = "defense_of",
	        DOUBLE = "double",
	        DOUBLE_PLAY = "doubleplay",
	        DOUBLE_PLAYS = "doublePlays",
	        DOUBLES_SHORT = "2B",
	        DOUBLES_LONG = "doubles",
	        DURABILITY_LABEL = "durability",
	        EARNED_RUN_AVERAGE = "ERA",
	        EARNED_RUNS_SHORT = "ER",
	        ELIGIBILITY = "eligibility",
	        FEILDING_LABEL = "fielding",
	        FIRST_BASE_PREFIX = "fbm",
	        FIRST_NAME = "firstname",
	        GDP_LABEL = "gdp",
	        H_LABEL = "H",
	        HAB_LABEL = "hab",
	        HB_LABEL = "hb",
	        HBP_LABEL = "hbp",
	        HEIGHT_LABEL = "height",
	        HIT_BATTER_LONG = "hitbatter",
	        HIT_BATTER_SHORT = "HB",
	        HITS_LABEL = "hits",
	        HOMERUN = "homerun",
	        HR_LABEL = "hr",
	        HRS_LABEL = "hrs",
	        IBB_LABEL = "ibb",
	        INITIAL_RUNS = "initial_runs",
	        INTENTIONALWALKS = "intentionalWalks",
	        ISO_LABEL = "iso",
	        L_LABEL = "L",
	        LAST_NAME = "lastname",
	        MF_LABEL = "mf",
	        MOVE_TO_FIRST = "movetofirst",
	        NOATBAT = "noatbat",
	        OPS_LABEL = "ops",
	        P_INDEX = "p_index",
	        PA_LABEL = "pa",
	        PASSED_BALLS = "passedBalls",
	        PB_LABEL = "pb",
	    	PITCHER_2B = "pitcher2B",
	        PITCHER_3B = "pitcher3B",
	        PITCHER_BATTERS_FACED = "pitcherBattersFaced",
	        PITCHER_BB = "pitcherBB",
	        PITCHER_BK = "pitcherBK",
	        PITCHER_ER = "pitcherER",
	        PITCHER_GAME = "pitcher_game",
	        PITCHER_HB = "pitcherHB",
	        PITCHER_HITS = "pitcherHits",
	        PITCHER_HR = "pitcherHR",
	        PITCHER_IBB = "pitcherIBB",
	        PITCHER_IR = "pitcherInheritedRunners",
	        PITCHER_IP = "pitcherIP",
	        PITCHER_LEFT_ON_BASE = "pitcherLeftOnBase",
	        PITCHER_RUNS = "pitcherRuns",
	        PITCHER_SACFLY = "pitcherSacfly",
	        PITCHER_SACRIFICE = "pitcherSacrifice",
	        PITCHER_SO = "pitcherSO",
	        PITCHER_STAT = "pitcher_stat",
	        PITCHER_SUB = "pitcher_sub",
	        PITCHER_SUB_HOME = "p1sub",
	        PITCHER_SUB_AWAY = "p2sub",
	        PITCHER_WP = "pitcherWP",
	        PLATOON_LABEL = "platoon",
	        PRIME_LABEL = "prime",
	        RBI_LABEL = "rbi",
	        RES_LABEL = "res",
	        RUNS_LABEL_SHORT = "r",
	        RUNS_LABEL_LONG = "runs",
	        SACFLY = "sacfly",
	        SB_LABEL = "sb",
	        SBP_LABEL = "sbp",
	        SECA_LABEL = "seca",
	        SECOND_BASE_PREFIX = "sbm",
	        SF_LABEL = "sf",
	        SH_LABEL = "sh",
	        SINGLE = "single",
	        SO_LABEL = "so",
	        SPG_LABEL = "SPG",
	        SS_LABEL = "ss",
	        STARTING_PLAYER = "starting_player",
	        STEAL_LABEL = "steal",
	        STOLEN_BASES = "stolenBases",
	        STRIKEOUT_LABEL = "strikeout",
	        STRIKEOUT_RATING = "SR",
	        SUB_REQUIRED = "sub_required",
	        SUBBABLE = "subbable",
	        SUBSTITUTIONS = "substitutions",
	        SV_LABEL = "SV",
	        TB_LABEL = "tb",
	        THIRD_BASE_PREFIX = "tbm",
	        THROW_RATING = "throw_rating",
	        THROWS_LABEL_LONG = "throws",
	        TIMES_HIT = "timesHit",
	        TOTAL_SHORT = "ttl",
	        TRIPLE = "triple",
	        TRIPLE_PLAY = "tripleplay",
	        TRIPLE_PLAYS = "tripleplays",
	        TRIPLES_SHORT = "3B",
	        TRIPLES_LONG = "triples",
	        W_LABEL = "W",
	        WALKS_LABEL = "walks",
	        WEIGHT_LABEL = "weight",
	        WILD_LABEL = "wild",
	        WIN_LOSS = "wl",
	        WP_LABEL = "wp",
	        YEAR_LABEL = "year",

	        FIELDING_ONE = "1",
	        FIELDING_TWO = "2",
	        FIELDING_THREE = "3",
	        
	        DEFAULT_PITCHER_RATING = "0",
	        DEFAULT_BATTER_AB = "1.0",
	        DEFAULT_AVG = "1.000",
	        
	        INT_STR_ZERO = "0",
	        INT_STR_ONE = "1",
	        INT_STR_TWO = "2",
	        INT_STR_THREE = "3",
	        INT_STR_FOUR = "4",
	        INT_STR_FIVE = "5",
	        INT_STR_SIX = "6",
	        INT_STR_SEVEN = "7",
	        INT_STR_EIGHT = "8",
	        INT_STR_NINE = "9",
	        INT_STR_TEN = "10",
	        INT_STR_ELEVEN = "11",
	        INT_STR_EIGHTEEN = "18",
	        
	        PITCHER_2B_LONG = "pitcher_2b",
	        PITCHER_3B_LONG = "pitcher_3b",
	        PITCHER_BB_LONG = "pitcher_bb",
	        PITCHER_BB_SHORT = "pBB",
	        PITCHER_HITS_LONG = "pitcher_hits",
	        PITCHER_HITS_SHORT = "pH",
	        PITCHER_HR_LONG = "pitcher_hr",
	        PITCHER_IBB_LONG = "pitcher_ibb",
	        PITCHER_IR_LONG = "pitcher_ir",
	        PITCHER_LOB_LONG = "pitcher_lob",
	        PITCHER_LOB_SHORT = "LOB",
	        PITCHER_RUNS_LONG = "pitcher_runs",
	        PITCHER_RUNS_SHORT = "pR",
	        PITCHER_SF_LONG = "pitcher_sf",
	        PITCHER_SH_LONG = "pitcher_sh",
	        PITCHER_STRIKEOUT_LONG = "pitcher_strikeout",
	        PITCHER_STRIKEOUT_SHORT = "pSO",
	        POSITION_CATCHER_SHORT = "C",
	        POSITION_FIRST_BASE = "1B",
	        POSITION_SECOND_BASE = "2B",
	        POSITION_THIRD_BASE = "3B",
	        POSITION_SHORT_STOP = "SS",
	        POSITION_LEFT_FIELD = "LF",
	        POSITION_RIGHT_FIELD = "RF",
	        POSITION_CENTER_FIELD = "CF",
	        POSITION_DESIGNATED_HITTER = "DH",
	        POSITION_PITCHER_SHORT = "P",
	        POSITION_RELIEF_PITCHER = "P*",
	        POSITION_ATBAT = "AB",
	        POSITION_ONDECK = "OD",
	        POSITION_HITTER = "Hitter",
	        POSITION_PITCHER = "Pitcher",
	        POSITION_CATCHER = "Catcher",
	        POSITION_FIRST = "First",
	        POSITION_SECOND = "Second",
	        POSITION_THIRD = "Third",
	        POSITION_SHORT = "Short",
	        POSITION_LEFT = "Left",
	        POSITION_CENTER = "Center",
	        POSITION_RIGHT = "Right",
	        POSITION_ABBR_CATCHER = "PC",
	        POSITION_ABBR_FIRST_BASE = "P1B",
	        POSITION_ABBR_SECOND_BASE = "P2B",
	        POSITION_ABBR_THIRD_BASE = "P3B",
	        POSITION_ABBR_SHORTSTOP = "PSS",
	        POSITION_ABBR_LEFT_FIELD = "PLF",
	        POSITION_ABBR_RIGHT_FIELD = "PRF",
	        POSITION_ABBR_CENTER_FIELD = "PCF",
	        
	        NO_DESIGNATED_HITTER = "NDH",

	        POSITION_1ST_BASE_LABEL = "1st Base",
	        POSITION_2ND_BASE_LABEL = "2nd Base",
	        POSITION_3RD_BASE_LABEL = "3rd Base",
	        POSITION_CATCHER_LABEL = "Catcher",
	        POSITION_CENTER_FIELD_LABEL = "CenterField",
	        POSITION_CENTER_FIELDER = "Center Fielder",
	        POSITION_CENTERFIELD_LABEL = "Centerfield",
	        POSITION_DESIGNATED_HITTER_LABEL = "Designated Hitter",
	        POSITION_DESIGNATEDHITTER_LABEL = "DesignatedHitter",
	        POSITION_FIRST_BASE_LABEL = "FirstBase",
	        POSITION_FIRST_BASEMAN = "First Baseman",
	        POSITION_LEFT_FIELD_LABEL = "LeftField",
	        POSITION_LEFT_FIELDER = "Left Fielder",
	        POSITION_LEFTFIELD_LABEL = "Leftfield",
	        POSITION_PITCHER_LABEL = "Pitcher",
	        POSITION_RIGHT_FIELD_LABEL = "RightField",
	        POSTION_RIGHT_FIELDER = "Right Fielder",
	        POSITION_RIGHTFIELD_LABEL = "Rightfield",
	        POSITION_SECOND_BASE_LABEL = "SecondBase",
	        POSITION_SECOND_BASEMAN = "Second Baseman",
	        POSITION_SHORT_STOP_LABEL = "Shortstop",
	        POSITION_THIRD_BASE_LABEL = "ThirdBase",
	        POSITION_THIRD_BASEMAN = "Third Baseman",
	        STAT_REF_EARNED_OUT_TRACKING = "earned_out_tracking",
	        STAT_REF_EARNED_RUNNERS = "earned_runners",
	        STAT_REF_KEY_IP = "ip",
	        STAT_REF_KEY_OPTP = "optp",
	        STAT_REF_RATING = "rating",
	        
	        ORDINAL_GENERAL = "th",
	        ORDINAL_FIRST = "st",
	        ORDINAL_SECOND = "nd",
	        ORDINAL_THIRD = "rd",
	        
	        PITCHER_GRADE_AB = "A&B",
	        PITCHER_GRADE_AC = "A&C",
	        PITCHER_GRADE_A = "A",
	        PITCHER_GRADE_B = "B",
	        PITCHER_GRADE_C = "C",
	        PITCHER_GRADE_D = "D",
	        
	        PEDRO = "Pedro (AI)",
	       	MALCOLM = "Malcolm (AI)";
	
	public static final Integer 
			INT_NEG_TWO = -2,
			INT_NEG_ONE = -1,
			INT_ZERO = 0,
			INT_ONE = 1,
			INT_TWO = 2,
			INT_THREE = 3,
			INT_FOUR = 4,
			INT_FIVE = 5,
			INT_SIX = 6,
			INT_EIGHT = 8,
			INT_NINE = 9,
			INT_TEN = 10,
			INT_SIXTEEN = 16,
			INT_ONE_HUNDRED = 100,
					
			TYPE_MAL = 1,
			TYPE_PEDRO = 2,
			
			BASE_STATUS_NONE_LOADED = 10,
			BASE_STATUS_1ST_ONLY = 11,
			BASE_STATUS_2ND_ONLY = 12,
			BASE_STATUS_3RD_ONLY = 13,
			BASE_STATUS_1ST_2ND_LOADED = 14,
			BASE_STATUS_1ST_3RD_LOADED = 15,
			BASE_STATUS_2ND_3RD_LOADED = 16,
			BASE_STATUS_ALL_LOADED = 17;
	
	public static final int 
			PITCHER_GRADE_AB_INT = 9,
			PITCHER_GRADE_AC_INT = 8,
			PITCHER_GRADE_A_INT = 7,
			PITCHER_GRADE_B_INT = 6,
			PITCHER_GRADE_C_INT = 5,
			PITCHER_GRADE_D_INT = 4,
			NO_PITCHER_GRADE = 0,
			
			PITCHER_DEFENSE_RATING = 0,
			CATCHER_DEFENSE_RATING = 4,
			FIRST_BASE_DEFENSE_RATING = 1,
			SECOND_BASE_DEFENSE_RATING = 4,
			THIRD_BASE_DEFENSE_RATING = 2,
			SHORTSTOP_DEFENSE_RATING = 5,
			OUTFIELD_DEFENSE_RATING = 0,
			
			AI_ID = -1,
	
			LINEUP_JSON_STATUS_CODE = -2,
			
			MAX_STRIKEOUTS = 3,
			MAX_LINEUP_POSITION = 9,
			MAX_NORMAL_INNINGS = 9,
			
			MESSAGE_INFO = 3,
			MESSAGE_PBP = 4,
			MESSAGE_DEBUG = 5,
			MESSAGE_TRACE = 6;
	
	// DICE SERVICE LABELS
	public static final String 
			APPLY_LABEL = "apply",
			AVAILABLE = "available",
			BASE_STATUS_LABEL = "base_status",
			BUSY_LABEL = "busy",
			BATTER_INJURED = "batter is injured",
			CATCHER_INJURED = "catcher injured",
			COMMA = ",",
			DICE_GENERATE = "dice_generate",
			DICE_GENERATE_ADVANCE = "dice_generate_advance",
			DICE_GENERATE_MODIFIER1 = "dice_generate_modifier1",
			DICE_LABEL = "dice",
			DICE_NAME_LABEL = "dice_name",
			DICE_RESULT = "dice_result",
			DICE_RESULT_RATING = "dice_result_rating",
			EJECTED_LABEL = "ejected",
			EQUALS_LABEL = "=",
			EMPTY_STRING = "",
			ERROR_LABEL = "error",
			ERRORS_LABEL = "errors",
			GRADE_MODIFIER = "grade_modifier",
			HITRUN_LABEL = "hitrun",
			HYPHEN_LABEL = "-",
			INJURY_LABEL = "injured",
			INJURY_LABEL_2 = "collide",
			INJURY_LABEL_3 = "struggling",
			LEAVE_LABEL = "leave",
			LEAVING_LABEL = "leaving",
			MESSAGE_LABEL = "message",
			NORMAL_LABEL = "normal",
			OPPONENT_INACTIVE = "opponent_inactive",
			ORIGINAL_POOL = "originalPool",
			PAGE = "page",
			PARSE_CONDITION_LABEL = "parse_condition",
			PARSER = "parser",
			PAUSED_LABEL = "paused",
			PERIOD = ".",
			PINCH_HITTER_LABEL = "PH",
			PINCH_LABEL = "pinch",
			PINCH_RUNNER_LABEL = "PR",
			PITCHER_ERB_LABEL = "pitcher_erb",
			PITCHER_GRADE_LABEL = "pg",
			PITCHER_IR_LABEL = "pitcher_ir",
			PLAYED = "played",
			PLAYING_LABEL = "playing",
			POOL = "pool",
			PUT_OUT = "putOut",
			RAIN = "rain",
			RELIEF_PITCHER_GRADE_LABEL_LONG = "relief_pg",
			RELIEF_PITCHER_GRADE_LABEL_SHORT = "rpg",
			RESULT_COLUMN = "result_column",
			RESULT_ID_LABEL = "result_id",
			RESULT_LABEL = "result",
			ROLL = "roll",
			SA_LABEL = "sa",
			SACRIFICE_LABEL = "sacrifice",
			SECTION = "section",
			SEMICOLON = ";",
			SESSION_INVALID = "session_invalid",
			SID_LABEL= "sid",
			TEAM_LABEL = "team";
	
	// GAME SERVICE LABELS
	public static final String
			AI_GAME = "aiGame",
			AWAY_BATTER = "away_batter",
			AWAY_E = "away_e",
			AWAY_INNINGS = "away_innings",
			AWAY_H = "away_h",
			AWAY_R = "away_r",
			AWAYTEAMYEAR = "awayTeamYear",
			AWAY_YEAR = "away_year",
			BALLS_LABEL = "balls",
			BATCOUNT_LABEL = "batcount",
			BATTING_ORDER = "batting_order",
			BT_LABEL = "bt",
			CARD_LABEL = "card",
			CD_STAT_PREFIX = "cd_",
			COLLECTIONS = "collections",
			CONSOLE_LABEL = "console",
			COUNT_LABEL = "count",
			CSO_LABEL = "cso",
			CURRENT_BATTING_POSITION = "current_batting_position",
			CURRENTLY_lABEL = "currently",
			DEFAULT_LABEL = "default",
			DEFENSE = "defense",
			DELETE = "DELETE",
			EARNED_OUTS_LABEL = "earned_outs",
			ERA_LABEL = "era",
			EVENTS_LABEL = "events",
			FIELD_LABEL = "field",
			FORCE_RBI = "force_rbi",
			GID_LABEL = "gid",
			HOLD_R1 = "hold_r1",
			HOLD_R3 = "hold_r3",
			HOME_BATTER = "home_batter",
			HOME_E = "home_e",
			HOME_INNINGS = "home_innings",
			HOME_H = "home_h",
			HOME_R = "home_r",
			HOMETEAMYEAR = "homeTeamYear",
			HOME_YEAR = "home_year",
			INNING = "inning",
			KEY_LABEL = "key",
			LIMIT_LABEL = "limit",
			LINEUP_COUNT = "lineup_count",
			LINEUP_LABEL = "lineup",
			LINEUP_ID = "lineupId",
			LINEUP_LIST = "lineup_list",
			LINEUP_LIST_STR = "lineup_list_str",
			LONGNAME = "longname",
			MOVES_LABEL = "moves",
			MULTIPLE_LABEL = "multiple",
			NO_DEFENSE = "no_defense",
			NO_OFFENSE = "no_offense",
			NULL_LABEL = "null",
			OFF_LABEL = "off",
			OFFENSE = "offense",
			OLD_MOVES = "old_moves",
			ON_LABEL = "on",
			OUTPUT_LABEL = "output",
			PASSED_BALL = "passedball",
			PITCH_LABEL = "pitch",
			PITCHER_ERA = "pitcherERA",
			PITCHER_ID = "pitcherId",
			PITCHING_CHANGES = "pitching_changes",
			PLAYSAFER = "playsafer",
			PLAYSAFER_1 = "playsafer1",
			PLAYSAFER_2 = "playsafer2",
			PLAYSAFER_3 = "playsafer3",
			PLAYSAFE_R = "playsafe_r",
			PLAYESAFE_R1 = "playsafe_r1",
			PLAYESAFE_R2 = "playsafe_r2",
			PLAYESAFE_R3 = "playsafe_r3",
			PREVIOUS_CARD = "previousCard",
			REST_LABEL = "REST",
			RESULTS_DELAY = "resultsDelay",
			REVERT_LABEL = "revert",
			SCORE_LABEL = "score",
			SELECTING_AWAY_LABEL = "selecting_away",
			SHARED_LABEL = "shared",
			SOLITAIRE_GAME = "solitaireGame",
			SPEED_LABEL = "speed",
			STRETCH_LABEL = "stretch",
			STRIKES_LABEL = "strikes",
			SWINGS_LABEL = "swings",
			TEAM_NAME = "team_name",
			THROW = "throw",
			TOP_LABEL = "top",
			TYPE_LABEL = "type",
			R1_LABEL = "r1",
			R2_LABEL = "r2",
			R3_LABEL = "r3",
			RUNNER_1 = "runner1",
			RUNNER_2 = "runner2",
			RUNNER_3 = "runner3",
			RUNNER_4 = "runner4",
			UNSEEN_LABEL = "unseen",
			URL_LABEL = "url",
			VALUE_LABEL = "value",
			WALK_LABEL = "walk";
	
	// STRING OUTPUTS
	public static final String
			ADVANCED_GRADE = " has advanced to Grade ",
			ASTERISK = "*",
			BOTTOM_OF_THE = "It is the bottom of the ",
			COLON = ":",
			COMMA_SEPARATE = ", ",
			CLOSED_PARENTHESIS = ")",
			CTA_TAG_CLOSED = "</cta>",
			CTH_TAG_CLOSED = "</cth>",
			CTA_TAG_OPEN = "<cta>",
			CTH_TAG_OPEN = "<cth>",
			DID_NOT_END_HOME_PLATE = " did not end at home plate",
			ELLIPSIS = "...",
			EMPTY_SPACE = " ",
			ENDED_HOME_PLATE = " ended at home plate",
			EXCLAMATION_POINT = "!",
			FOR_STRING = " for ",
			FORMAT_SINGLE_CHARACTER = "#",
			FORMAT_THOUSANDS = "#,###.",
			FORWARD_SLASH = "/",
			GRADE_STRING = "Grade ",
			GRADE_RETURN = " has returned to his original grade.",
			INJURY_DISABLED_MESSAGE = "Injury disabled. Roll Again.",
			LINEUP_CHANGE = "Lineup Change: ",
			LOG_CTA_TAG_CLOSED = "</log_cta>",
			LOG_CTH_TAG_CLOSED = "</log_cth>",
			LOG_CTA_TAG_OPEN = "<log_cta>",
			LOG_CTH_TAG_OPEN = "<log_cth>",
			LOG_INNING_TAG_CLOSED = "</log_inning>",
			LOG_INNING_TAG_OPEN = "<log_inning>",
			LOOKING_BOXSCORE = " looking at box score",
			LOOKING_LINEUP = " looking at lineup",
			MANUAL_DICE_ROLL = "User has manually entered dice roll",
			MOVE_INFO = "Move information: ",
			MOVES_TO = " moves to ",
			MULTIPLE_RUNS_STRING = " runs score.",
			MULIPLE_WALK_OFFS_STRING = " walk offs score.",
			NO_RUNNERS = "No runner started at ",
			ON_DECK = " is on deck!",
			OPEN_PARENTHESIS = "(",
			PAUSE_TAG_CLOSED = "</pause>",
			PAUSE_TAG_OPEN = "<pause>",
			PAUSE_IN_PLAY = "Pause In Play",
			PLAYER_ADVANCES_TO_PLATE = " advances to the plate!",
			QUOTATION_MARK = "\"",
			RAIN_DISABLED_MESSAGE = "Rain disabled. Roll Again.",
			SCORING_INFORMATION = "Scoring Information, ",
			SCORING_INFO_HOME = " H:",
			SCORING_INFO_NOW = "Now - A:",
			SCORING_INFO_WAS = "Was - A:",
			SINGLE_RUN_STRING = "1 run scores.",
			SINGLE_WALK_OFF_STRING = "1 walk off scored.",
			SPECIAL_PLAY = "Special Play!",
			SUBSTITUTION_TAG_CLOSED = "</substitution>",
			SUBSTITUTION_TAG_OPEN = "<substitution>",
			TAKES_LEAD = " takes the lead!",
			TEAM_WON_AWAY = "Away team wins!",
			TEAM_WON_HOME = "Home team wins!",
			TOP_OF_THE = "It is the top of the ",
			UNDERSCORE = "_",
			VERTICAL_BAR = "|",
			
			ERROR_BATTER = "Batter threw null pointer exception:  ",
			ERROR_BATTER_AVATAR = "Batter Avatar threw null pointer exception:  ",
			ERROR_GET_DICE = "getDice() threw null pointer exception: ",
			ERROR_ADICE2 = "adice2 threw null pointer exception:",
			ERROR_NULL_POINTER = "Response threw null pointer: ";
	
	public static final String 
			MAPPING_GAME = "game/";
	
	public static final char 
			COMMA_CHAR = ',',
			EMPTY_SPACE_CHAR = ' ',
			HYPHEN_CHAR = '-',
			VERTICAL_BAR_CHAR = '|';
}

