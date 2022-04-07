package com.lixar.apba.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.lixar.apba.core.util.*;
import com.lixar.apba.domain.*;
import com.lixar.apba.repository.*;
import com.lixar.apba.service.util.AutoplayUtil;
import com.lixar.apba.service.util.EntityManagerUtil;
import com.lixar.apba.service.util.MicroManagerFactory;
import com.lixar.apba.service.util.UrlFactory;
import com.lixar.apba.web.ModelConstants.Side;
import com.lixar.apba.web.NotFoundException;
import com.lixar.apba.web.SessionKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.lixar.apba.core.util.APBAHelper.isAutoplayGame;
import static com.lixar.apba.core.util.GameConstants.*;
import static com.lixar.apba.repository.AvatarRepository.MISSING_STAT;
import static com.lixar.apba.web.ModelConstants.DEFAULT_CONTEXT;
import static com.lixar.apba.web.SessionKeys.*;
import static com.lixar.apba.service.MappedConstants.*;

@Service
public class APBAGameService {

	private final Logger log = LoggerFactory.getLogger(APBAGameService.class);

	@Inject
	private GameNpcRepository gameNpcRepository;

	@Inject
	private AvatarDiceRepository avatarDiceRepository;

	@Inject
	private AvatarRepository avatarRepository;

	@Inject
	private DiceResultRepository diceResultRepository;

	@Inject
	private GameReadyRepository gameReadyRepository;

	@Inject
	private ExternalGameRepository externalGameRepository;

	@Inject
	private GameLineupRepository gameLineupRepository;

	@Inject
	private GameRepository gameRepository;

	@Inject
	private GameSubstitutionRepository gameSubstitutionRepository;

	@Inject
	private GameService gameService;
	
	@Inject
	private NpcService npcService;

	@Inject
	private EndGameService endGameService;

	@Inject
	private ApbaSessionRepository sessionRepository;

	@Inject
	private GameConsoleRepository gameConsoleRepository;

    @Inject
    private MicroManagerFactory microManagerFactory;

    @Inject
	private APBADiceService apbaDiceService;

	@Inject
	private GameStatusService gameStatusService;

	@Inject
	private FormatService formatService;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
	private EntityManagerUtil entityManagerUtil;

	private static final Map<Integer, Integer> inningsMap = new LinkedHashMap<>();

	@Transactional(readOnly = true)
	public Map<String, Object> getPlayerCardAction(Integer id) throws JSONException, JsonMappingException, JsonProcessingException {
		Map<String, Object> playerCard = new LinkedHashMap<>();

		GameNpc npc = gameNpcRepository.findById(id).get();
		Avatar npca = npc.getAvatar();
		avatarRepository.makeResExpire(npca.getId());
		
		Dice dice = avatarDiceRepository.findByAvatarAndPriority(npca, 1).getDice();
		AvatarDice adice2 = avatarDiceRepository.findByAvatarAndPriority(npca, 2);
		Dice dice2 = null;
		if (adice2 != null) {
			dice2 = adice2.getDice();
		}
		
		JSONObject eligibility = new JSONObject(avatarRepository.getStat(npca, "cd_epos"));

		playerCard.put(NAME_LABEL, npc.getFullName());
		playerCard.put(LAST_NAME, npc.getLastName());
		playerCard.put(FIRST_NAME, npc.getFirstName());
		playerCard.put(ID_LABEL, npc.getId());
		playerCard.put(POS_LABEL, gameNpcRepository.getStat(npc, POSITION_LABEL));
		playerCard.put(PRIME_LABEL, avatarRepository.getStat(npca, CD_POS));
		playerCard.put(STAT_REF_RATING, gameNpcRepository.getStat(npc, STAT_REF_RATING));
		playerCard.put(BATS_LABEL, avatarRepository.getStat(npca, CD_BATS));
		playerCard.put(THROWS_LABEL_LONG, avatarRepository.getStat(npca, CD_THROWS));
		playerCard.put(ELIGIBILITY, eligibility);
		playerCard.put(SPEED_LABEL, OPEN_PARENTHESIS + avatarRepository.getStat(npca, CD_SPEED) + CLOSED_PARENTHESIS);
		playerCard.put(INJURY_LABEL, avatarRepository.getStat(npca, CD_J));
		playerCard.put(STEAL_LABEL, avatarRepository.getStat(npca, CD_ST) + avatarRepository.getStat(npca, CD_SSN));
		playerCard.put(SP_LABEL.toLowerCase(), avatarRepository.getStat(npca, CD_SR));
		playerCard.put(ARM_LABEL, avatarRepository.getStat(npca, CD_ARM));
		playerCard.put(PB_LABEL, avatarRepository.getStat(npca, CD_PB));
		playerCard.put(CD_TH_LABEL, avatarRepository.getStat(npca, CD_TH));
		playerCard.put(HB_LABEL, avatarRepository.getStat(npca, CD_HB));
		playerCard.put(WP_LABEL, avatarRepository.getStat(npca, CD_WP));
		playerCard.put(GRADE_LABEL_LONG, avatarRepository.getCachedRes(npca, PITCHER_GRADE_LABEL));
		playerCard.put(RGRADE_LABEL, avatarRepository.getCachedRes(npca, RELIEF_PITCHER_GRADE_LABEL_LONG));
		playerCard.put(HEIGHT_LABEL, avatarRepository.getCachedRes(npca, CD_HEIGHT));
		playerCard.put(WEIGHT_LABEL, avatarRepository.getCachedRes(npca, CD_WEIGHT));
		playerCard.put(BORN_LABEL, avatarRepository.getCachedRes(npca, CD_BORN));
		playerCard.put(FEILDING_LABEL, avatarRepository.getStat(npca, CD_CARD_RATING));

		String grade = npcService.convertPitcherGrade(PHPHelper.toInt((String) playerCard.get(GRADE_LABEL_LONG)));
		playerCard.put(GRADE_LABEL_LONG, grade == null ? PITCHER_GRADE_D : grade);

		String rgrade = npcService.convertPitcherGrade(PHPHelper.toInt((String) playerCard.get(RGRADE_LABEL)));
		if (rgrade != null) {
			playerCard.put(GRADE_LABEL_LONG, playerCard.get(GRADE_LABEL_LONG) + OPEN_PARENTHESIS + rgrade + ASTERISK + CLOSED_PARENTHESIS);
		}

		// Conversion note: the original code was if ($result['pos'] == '-777' || $result['rating'] = '-777') {
		// But this doesn't actually test anything - the rating is not compared - it is assigned, so this always runs
		// and works because of a bug
		playerCard.put(POS_LABEL, playerCard.get(ELIGIBILITY));
		playerCard.put(STAT_REF_RATING, EMPTY_STRING);

		if (eligibility.has(POSITION_PITCHER_SHORT)) {
			playerCard.put(STRIKEOUT_LABEL, avatarRepository.getStat(npca, CD_STRIKEOUT));
			playerCard.put(CONTROL_LABEL, avatarRepository.getStat(npca, CD_CONTROL));
		} else {
			playerCard.put(STRIKEOUT_LABEL, EMPTY_STRING);
			playerCard.put(CONTROL_LABEL, EMPTY_STRING);
		}

		Map<String, Object> batterStat = new LinkedHashMap<>();
		for (Entry<String, String> entry : BATTER_STAT_MAP.entrySet()) {
			String bs = entry.getKey();
			String bn = entry.getValue();
			batterStat.put(bn, avatarRepository.getStat(npca, bs));
		}
		playerCard.put(BATTER_STAT, batterStat);

		Map<String, Object> pitcherStat = new LinkedHashMap<>();
		for (Entry<String, String> entry : PITCHER_STAT_MAP.entrySet()) {
			String bs = entry.getKey();
			String bn = entry.getValue();
			pitcherStat.put(bn, avatarRepository.getStat(npca, bs));
		}
		playerCard.put(PITCHER_STAT, pitcherStat);

		// Conversion note: because of the bug above that always sets pos = eligibility, this section is probably never hit
		if (playerCard.get(POS_LABEL) instanceof String) {
			switch ((String) playerCard.get(POS_LABEL)) {
				case POSITION_PITCHER_SHORT:
					playerCard.put(POS_LABEL, POSITION_PITCHER_LABEL);
					break;
				case POSITION_CATCHER_SHORT:
					playerCard.put(POS_LABEL, POSITION_CATCHER_LABEL);
					break;
				case POSITION_FIRST_BASE:
					playerCard.put(POS_LABEL, POSITION_1ST_BASE_LABEL);
					break;
				case POSITION_SECOND_BASE:
					playerCard.put(POS_LABEL, POSITION_2ND_BASE_LABEL);
					break;
				case POSITION_THIRD_BASE:
					playerCard.put(POS_LABEL, POSITION_3RD_BASE_LABEL);
					break;
				case POSITION_SHORT_STOP:
					playerCard.put(POS_LABEL, POSITION_SHORT_STOP_LABEL);
					break;
				case POSITION_LEFT_FIELD:
					playerCard.put(POS_LABEL, POSITION_LEFTFIELD_LABEL);
					break;
				case POSITION_CENTER_FIELD:
					playerCard.put(POS_LABEL, POSITION_CENTERFIELD_LABEL);
					break;
				case POSITION_RIGHT_FIELD:
					playerCard.put(POS_LABEL, POSITION_RIGHTFIELD_LABEL);
					break;
				case POSITION_DESIGNATED_HITTER:
					playerCard.put(POS_LABEL, POSITION_DESIGNATED_HITTER_LABEL);
					break;
			}
		}


		Map<String, Object> diceField = new LinkedHashMap<>();
		List<DiceResult> dierolls1 = diceResultRepository.findByDice(dice.getId());
		for (DiceResult result1 : dierolls1) {
			diceField.put(result1.getRequirement(), result1.getResultText());
		}
		if (dice2 != null) {
			List<DiceResult> dierolls2 = diceResultRepository.findByDice(dice2.getId());
			for (DiceResult result2 : dierolls2) {
				diceField.put(result2.getRequirement(), (String) diceField.get(result2.getRequirement()) + HYPHEN_CHAR + result2.getResultText());
			}
		}
		playerCard.put(DICE_LABEL, diceField);

		//Filter stats for detailed card's player card (this has slightly different info)
		APBAHelper.filterStats(playerCard, Arrays.asList(PITCHER_STAT, BATTER_STAT), null, Arrays.asList(EARNED_RUN_AVERAGE));
		return playerCard;
	}

	@Transactional(readOnly = true)
	public Map<String, Object> detailedPlayerCardAction(Integer id) {
		Map<String, Object> result = new LinkedHashMap<>();

		GameNpc npc = gameNpcRepository.findById(id).get();
		Avatar npca = npc.getAvatar();
		avatarRepository.makeResExpire(npca.getId());

		Map<String, Object> player_info = new LinkedHashMap<>();
		player_info.put(NAME_LABEL, npc.getFullName());
		player_info.put(LAST_NAME, npc.getLastName());
		player_info.put(FIRST_NAME, npc.getFirstName());
		player_info.put(ID_LABEL, npc.getId());
		player_info.put(HEIGHT_LABEL, avatarRepository.getStat(npca, CD_HEIGHT));
		player_info.put(WEIGHT_LABEL, avatarRepository.getStat(npca, CD_WEIGHT));
		player_info.put(BORN_LABEL, avatarRepository.getStat(npca, CD_AGE));

		result.put(PLAYER_LABEL, player_info);

		Map<String, Object> offense_info = new LinkedHashMap<>();
		offense_info.put(BATS_LABEL, avatarRepository.getStat(npca, CD_BATS));
		offense_info.put(TYPE_LABEL, avatarRepository.getStat(npca, CD_HITS));
		offense_info.put(THROWS_LABEL_LONG, avatarRepository.getStat(npca, CD_THROWS));
		offense_info.put(PLATOON_LABEL, avatarRepository.getStat(npca, CD_PLATOON_R));
		offense_info.put(HR_LABEL, avatarRepository.getStat(npca, CD_HR));
		offense_info.put(SPEED_LABEL, avatarRepository.getStat(npca, CD_SPEED));
		offense_info.put(STEAL_LABEL, avatarRepository.getStat(npca, CD_ST));
		offense_info.put(SSN_LABEL, avatarRepository.getStat(npca, CD_SSN));
		offense_info.put(INJURY_LABEL, avatarRepository.getStat(npca, CD_J));

		result.put(OFFENSE, offense_info);

		Map<String, Object> defense_info = new LinkedHashMap<>();
		defense_info.put(PITCHER_LABEL, avatarRepository.getStat(npca, PITCHER_RATING_SHORT));
		defense_info.put(CATCHER_LABEL, avatarRepository.getStat(npca, CATCHER_RATING_SHORT));
		defense_info.put(FIRST_LABEL, avatarRepository.getStat(npca, FIRST_BASE_RATING_SHORT));
		defense_info.put(SECOND_LABEL, avatarRepository.getStat(npca, SECOND_BASE_RATING_SHORT));
		defense_info.put(THIRD_LABEL, avatarRepository.getStat(npca, THIRD_BASE_RATING_SHORT));
		defense_info.put(SHORT_LABEL, avatarRepository.getStat(npca, SHORTSTOP_RATING_SHORT));
		defense_info.put(OF_LABEL, avatarRepository.getStat(npca, OUTFIELD_RATING_SHORT));
		defense_info.put(ARM_LABEL, avatarRepository.getStat(npca, CD_ARM));
		defense_info.put(PASSED_BALL, avatarRepository.getStat(npca, CD_PB));
		defense_info.put(THROW, avatarRepository.getStat(npca, CD_TH));

		result.put(DEFENSE, defense_info);

		Map<String, String> pitcher_list = new LinkedHashMap<>();
		pitcher_list.put(SP_ERA, ERA_LABEL);
		pitcher_list.put(SP_W, GAMES_WON);
		pitcher_list.put(SP_L, GAMES_LOST);
		pitcher_list.put(SP_PCT, WIN_PERCENTAGE);
		pitcher_list.put(SP_G, GAMES_LABEL);
		pitcher_list.put(SP_GS, GAMES_STARTED);
		pitcher_list.put(SP_IP, INNINGS_LABEL);
		pitcher_list.put(SP_R, RUNS_LABEL_LONG);
		pitcher_list.put(SP_ER, EARNED_RUNS);
		pitcher_list.put(SP_HR, HOME_RUNS);
		pitcher_list.put(SP_SO, STRIKEOUTS);
		pitcher_list.put(SP_BB, WALKS_LABEL);
		pitcher_list.put(SP_IBB, INTENTIONAL_WALKS);
		pitcher_list.put(SP_HB, HIT_BATTERS);
		pitcher_list.put(SP_WP, WILD_PITCHES);
		pitcher_list.put(SP_BK, BALKS_LABEL);
		pitcher_list.put(SP_H, HITS_LABEL);
		pitcher_list.put(SP_SV, GAMES_SAVED);
		pitcher_list.put(SP_CG, GAMES_COMPLETED);

		Map<String, String> batter_list = new LinkedHashMap<>();
		batter_list.put(SB_G, GAMES_LABEL);
		batter_list.put(SB_PA, PLATE_APPEARANCES);
		batter_list.put(SB_AB, AT_BATS);
		batter_list.put(SB_H, HITS_LABEL);
		batter_list.put(SB_2B, DOUBLES_LONG);
		batter_list.put(SB_3B, TRIPLES_LONG);
		batter_list.put(SB_HR, HOMERUNS);
		batter_list.put(SB_TB, TOTAL_BASES);
		batter_list.put(SB_R, RUNS_LABEL_LONG);
		batter_list.put(SB_RBI, RBI_LABEL);
		batter_list.put(SB_SO, STRIKEOUTS);
		batter_list.put(SB_BB, WALKS_LABEL);
		batter_list.put(SB_IBB, INTENTIONAL_WALKS);
		batter_list.put(SB_HBP, BATTER_TIMES_HIT);
		batter_list.put(SB_SH, SACRIFICES);
		batter_list.put(SB_SF, SAC_FLIES);
		batter_list.put(SB_GDP, GDP_LABEL);
		batter_list.put(SB_AVG, AVG_LABEL);
		batter_list.put(SB_OBP, OBP_LABEL.toLowerCase());
		batter_list.put(SB_SLG, SLUG_LABEL);
		batter_list.put(SB_OPS, OPS_LABEL);
		batter_list.put(SB_ISO, ISO_LABEL);
		batter_list.put(SB_SECA, SECA_LABEL);
		batter_list.put(SB_TA, TA_LABEL);
		batter_list.put(SB_RC, RUNS_CREATED);
		batter_list.put(SB_RCG, RUNS_PER_GAME);
		batter_list.put(SB_ASB, H_BASES);
		batter_list.put(SB_CS, H_CAUGHT_STEALING);
		batter_list.put(SB_SBP, STOLEN_BASE_PERCENT);
		batter_list.put(SB_SB, STOLEN_BASES.toLowerCase());
		batter_list.put(SB_WP, WILD_PITCHES);

		// Conversion note: $future_defense_list not used

		Map<String, Object> batter_stat = new LinkedHashMap<>();
		result.put(BATTER_STAT, batter_stat);
		for (String bs : batter_list.keySet()) {
			String bn = batter_list.get(bs);
			batter_stat.put(bn, avatarRepository.getStat(npca, bs));
		}

		if (!avatarRepository.getStat(npca, PITCHER_RATING_SHORT).equals(DEFAULT_PITCHER_RATING)) {
			Map<String, String> pitcher_info = new LinkedHashMap<>();
			pitcher_info.put(GRADE_LABEL_LONG, avatarRepository.getCachedRes(npca, PITCHER_GRADE_LABEL));
			pitcher_info.put(RGRADE_LABEL, avatarRepository.getCachedRes(npca, RELIEF_PITCHER_GRADE_LABEL_LONG));
			pitcher_info.put(DURABILITY_LABEL, avatarRepository.getStat(npca, CD_FATIGUE));
			pitcher_info.put(STRIKEOUT_LABEL, avatarRepository.getStat(npca, CD_STRIKEOUT));
			pitcher_info.put(CONTROL_LABEL, avatarRepository.getStat(npca, CD_CONTROL));
			pitcher_info.put(HOMERUN, avatarRepository.getStat(npca, CD_HRA));
			pitcher_info.put(MOVE_TO_FIRST, avatarRepository.getStat(npca, CD_MF));
			pitcher_info.put(WILD_LABEL, avatarRepository.getStat(npca, CD_WP));
			pitcher_info.put(HIT_BATTER_LONG, avatarRepository.getStat(npca, CD_HB));
			pitcher_info.put(BALK_LONG, avatarRepository.getStat(npca, CD_BK));


			String grade = npcService.convertPitcherGrade(PHPHelper.toInt(pitcher_info.get(GRADE_LABEL_LONG)));
			pitcher_info.put(GRADE_LABEL_LONG, grade == null ? EMPTY_STRING : grade);

			grade = pitcher_info.get(GRADE_LABEL_LONG);
			String rgrade = npcService.convertPitcherGrade(PHPHelper.toInt(pitcher_info.get(RGRADE_LABEL)));
			if (rgrade != null) {
				pitcher_info.put(GRADE_LABEL_LONG, grade + OPEN_PARENTHESIS + rgrade + ASTERISK + CLOSED_PARENTHESIS);
			}

			result.put(PITCHER_LABEL, pitcher_info);

			Map<String, Object> pitcher_stat = new LinkedHashMap<>();
			result.put(PITCHER_STAT, pitcher_stat);
			for (String bs : pitcher_list.keySet()) {
				String bn = pitcher_list.get(bs);
				pitcher_stat.put(bn, avatarRepository.getStat(npca, bs));
			}

			int tmpi = PHPHelper.toInt(avatarRepository.getCachedRes(npca, STAT_REF_KEY_IP));
			int tmpr = tmpi % 3;

			Map<String, Object> pitcher_game = new LinkedHashMap<>();
			result.put(PITCHER_GAME, pitcher_game);

			pitcher_game.put(PITCHER_IP, ((tmpi - tmpr) / 3) + PERIOD + tmpr);
			pitcher_game.put(PITCHER_BATTERS_FACED, avatarRepository.getResBigDecimalOrZero(npca, BF_LABEL));
			pitcher_game.put(PITCHER_SACRIFICE, avatarRepository.getResBigDecimalOrZero(npca, PITCHER_SH_LONG));
			pitcher_game.put(PITCHER_SACFLY, avatarRepository.getResBigDecimalOrZero(npca, PITCHER_SF_LONG));
			pitcher_game.put(PITCHER_IR, avatarRepository.getResBigDecimalOrZero(npca, PITCHER_IR_LONG));
			pitcher_game.put(PITCHER_2B, avatarRepository.getResBigDecimalOrZero(npca, PITCHER_2B_LONG));
			pitcher_game.put(PITCHER_3B, avatarRepository.getResBigDecimalOrZero(npca, PITCHER_3B_LONG));
			pitcher_game.put(PITCHER_HR, avatarRepository.getResBigDecimalOrZero(npca, PITCHER_HR_LONG));
			pitcher_game.put(PITCHER_IBB, avatarRepository.getResBigDecimalOrZero(npca, PITCHER_IBB_LONG));
			pitcher_game.put(PITCHER_HITS, avatarRepository.getResBigDecimalOrZero(npca, PITCHER_HITS_LONG));
			pitcher_game.put(PITCHER_RUNS, avatarRepository.getResBigDecimalOrZero(npca, PITCHER_RUNS_LONG));
			pitcher_game.put(PITCHER_ER, avatarRepository.getResBigDecimalOrZero(npca, EARNED_RUNS_SHORT.toLowerCase()));
			pitcher_game.put(PITCHER_SO, avatarRepository.getResBigDecimalOrZero(npca, PITCHER_STRIKEOUT_LONG));
			pitcher_game.put(PITCHER_BB, avatarRepository.getResBigDecimalOrZero(npca, PITCHER_BB_LONG));
			pitcher_game.put(PITCHER_HB, avatarRepository.getResBigDecimalOrZero(npca, HIT_BATTER_LONG));
			pitcher_game.put(PITCHER_WP, avatarRepository.getResBigDecimalOrZero(npca, WP_LABEL));
			pitcher_game.put(PITCHER_BK, avatarRepository.getResBigDecimalOrZero(npca, BALK_LONG));
			pitcher_game.put(PITCHER_LEFT_ON_BASE, avatarRepository.getResBigDecimalOrZero(npca, PITCHER_LOB_LONG));
			pitcher_game.put(PITCHER_ERA, APBAHelper.calculateERA(avatarRepository.getResBigDecimalOrZero(npca, EARNED_RUNS_SHORT.toLowerCase()), avatarRepository.getResBigDecimalOrZero(npca, STAT_REF_KEY_IP)));
		}

		Map<String, Object> batter_game = new LinkedHashMap<>();
		result.put(BATTER_GAME, batter_game);

		batter_game.put(POSITION_ATBAT.toLowerCase(), avatarRepository.getResBigDecimalOrZero(npca, ATBAT));
		batter_game.put(RUNS_LABEL_SHORT, avatarRepository.getResBigDecimalOrZero(npca, RUNS_LABEL_SHORT));
		batter_game.put(RBI_LABEL, avatarRepository.getResBigDecimalOrZero(npca, RBI_LABEL));
		batter_game.put(DOUBLES_LONG, avatarRepository.getResBigDecimalOrZero(npca, DOUBLE));
		batter_game.put(TRIPLE, avatarRepository.getResBigDecimalOrZero(npca, TRIPLE));
		batter_game.put(HOMERUN, avatarRepository.getResBigDecimalOrZero(npca, HOMERUN));
		batter_game.put(H_LABEL.toLowerCase(), avatarRepository.getResBigDecimalOrZero(npca, SINGLE).add((BigDecimal) batter_game.get(DOUBLES_LONG)).add((BigDecimal) batter_game.get(TRIPLE)).add((BigDecimal) batter_game.get(HOMERUN)));
		batter_game.put(TB_LABEL, avatarRepository.getResBigDecimalOrZero(npca, SINGLE).add(new BigDecimal(2).multiply((BigDecimal) batter_game.get(DOUBLES_LONG))).add(new BigDecimal(3).multiply((BigDecimal) batter_game.get(TRIPLE))).add(new BigDecimal(4).multiply((BigDecimal) batter_game.get(HOMERUN))));
		batter_game.put(STRIKEOUT_LABEL, avatarRepository.getResBigDecimalOrZero(npca, STRIKEOUT_LABEL));
		batter_game.put(WALKS_LABEL, avatarRepository.getResBigDecimalOrZero(npca, BB_LABEL));
		batter_game.put(INTENTIONALWALKS, avatarRepository.getResBigDecimalOrZero(npca, IBB_LABEL));
		batter_game.put(TIMES_HIT, avatarRepository.getResBigDecimalOrZero(npca, HBP_LABEL));
		batter_game.put(SACRIFICE_LABEL, avatarRepository.getResBigDecimalOrZero(npca, SH_LABEL));
		batter_game.put(SACFLY, avatarRepository.getResBigDecimalOrZero(npca, SF_LABEL));
		batter_game.put(STOLEN_BASES, avatarRepository.getResBigDecimalOrZero(npca, STEAL_LABEL));
		batter_game.put(CAUGHT_STEALING, avatarRepository.getResBigDecimalOrZero(npca, CAUGHT_STEALING_SHORT));
		//Conversion note: added MathContext to divide operations to prevent arithmetic exceptions in cases like 1/3
		batter_game.put(BATTING_AVERAGE, ((BigDecimal) batter_game.get(H_LABEL.toLowerCase())).divide(new BigDecimal(DEFAULT_BATTER_AB).max((BigDecimal) batter_game.get(POSITION_ATBAT.toLowerCase())), DEFAULT_CONTEXT));
		batter_game.put(PUT_OUT, avatarRepository.getResBigDecimalOrZero(npca, PUT_OUT.toLowerCase()));
		batter_game.put(ASSIST_LABEL, avatarRepository.getResBigDecimalOrZero(npca, ASSIST_LABEL));
		batter_game.put(ERRORS_LABEL, avatarRepository.getResBigDecimalOrZero(npca, ERROR_LABEL));
		batter_game.put(DOUBLE_PLAYS, avatarRepository.getResBigDecimalOrZero(npca, DOUBLE_PLAY));
		batter_game.put(TRIPLE_PLAYS, avatarRepository.getResBigDecimalOrZero(npca, TRIPLE_PLAY));
		batter_game.put(PASSED_BALLS, avatarRepository.getResBigDecimalOrZero(npca, PB_LABEL));
        batter_game.put(BATTER_WP, avatarRepository.getResBigDecimalOrZero(npca, WP_LABEL));
		batter_game.put(PA_LABEL, ((BigDecimal) batter_game.get(POSITION_ATBAT.toLowerCase())).add((BigDecimal) batter_game.get(WALKS_LABEL)).add((BigDecimal) batter_game.get(INTENTIONALWALKS)).add((BigDecimal) batter_game.get(TIMES_HIT)).add((BigDecimal) batter_game.get(SACRIFICE_LABEL)));
		//Conversion note: added MathContext to divide operations to prevent arithmetic exceptions in cases like 1/3
		batter_game.put(OBP_LABEL.toLowerCase(), (((BigDecimal) batter_game.get(H_LABEL.toLowerCase())).add((BigDecimal) batter_game.get(WALKS_LABEL)).add((BigDecimal) batter_game.get(INTENTIONALWALKS)).add((BigDecimal) batter_game.get(TIMES_HIT))).divide(new BigDecimal(DEFAULT_BATTER_AB).max((BigDecimal) batter_game.get(PA_LABEL)), DEFAULT_CONTEXT));
		batter_game.put(SLG_LABEL.toLowerCase(), ((BigDecimal) batter_game.get(TB_LABEL)).divide(new BigDecimal(DEFAULT_BATTER_AB).max((BigDecimal) batter_game.get(POSITION_ATBAT.toLowerCase())), DEFAULT_CONTEXT));
		batter_game.put(OPS_LABEL, ((BigDecimal) batter_game.get(OBP_LABEL.toLowerCase())).add((BigDecimal) batter_game.get(SLG_LABEL.toLowerCase())));

		batter_game.put(ISO_LABEL, ((BigDecimal) batter_game.get(SLG_LABEL.toLowerCase())).subtract((BigDecimal) batter_game.get(BATTING_AVERAGE)));
		//Conversion note: added MathContext to divide operations to prevent arithmetic exceptions in cases like 1/3
		// No singles below? TODO - this is probably wrong
		batter_game.put(SECA_LABEL, ((BigDecimal) batter_game.get(DOUBLES_LONG)).add(new BigDecimal(2).multiply((BigDecimal) batter_game.get(TRIPLE))).add(new BigDecimal(3).multiply((BigDecimal) batter_game.get(HOMERUN))).add((BigDecimal) batter_game.get(WALKS_LABEL)).add((BigDecimal) batter_game.get(INTENTIONALWALKS)).add((BigDecimal) batter_game.get(STOLEN_BASES)).subtract((BigDecimal) batter_game.get(CAUGHT_STEALING)).divide(new BigDecimal(DEFAULT_BATTER_AB).max((BigDecimal) batter_game.get(POSITION_ATBAT.toLowerCase())), DEFAULT_CONTEXT));
		batter_game.put(ASB_LABEL, ((BigDecimal) batter_game.get(STOLEN_BASES)).add((BigDecimal) batter_game.get(CAUGHT_STEALING)));
		//Conversion note: added MathContext to divide operations to prevent arithmetic exceptions in cases like 1/3
		batter_game.put(SBP_LABEL, ((BigDecimal) batter_game.get(STOLEN_BASES)).divide(new BigDecimal(DEFAULT_BATTER_AB).max((BigDecimal) batter_game.get(CAUGHT_STEALING)), DEFAULT_CONTEXT));

		batter_game.put(OBP_LABEL.toLowerCase(), formatService.format((BigDecimal) batter_game.get(OBP_LABEL.toLowerCase()), 3));
		batter_game.put(SLG_LABEL.toLowerCase(), formatService.format((BigDecimal) batter_game.get(SLG_LABEL.toLowerCase()), 3));
		batter_game.put(OPS_LABEL, formatService.format((BigDecimal) batter_game.get(OPS_LABEL), 3));
		batter_game.put(SECA_LABEL, formatService.format((BigDecimal) batter_game.get(SECA_LABEL), 3));
		batter_game.put(SBP_LABEL, formatService.format((BigDecimal) batter_game.get(SBP_LABEL), 3));
		batter_game.put(ISO_LABEL, formatService.format((BigDecimal) batter_game.get(ISO_LABEL), 3));
		batter_game.put(BATTING_AVERAGE, formatService.format((BigDecimal) batter_game.get(BATTING_AVERAGE), 3));

		Map<String, Object> defense_total = new LinkedHashMap<>();
		result.put(DEFENSE_TOTAL, defense_total);
		for (String bs : POSITION_DEFENCE_STAT_MAP.keySet()) {
			String bn = POSITION_DEFENCE_STAT_MAP.get(bs);
			defense_total.put(bn, avatarRepository.getStat(npca, TOTAL_SHORT + UNDERSCORE + bs));
		}

		defense_total.put(PB_LABEL, avatarRepository.getStat(npca, CD_PB));

		fillPositionDefenseStatList(npca, PITCHER_RATING_SHORT, PITCHER_DEFENSE_RATING, result, DEFENSE_P);

		fillPositionDefenseStatList(npca, CATCHER_RATING_SHORT, CATCHER_DEFENSE_RATING, result, DEFENSE_C);

		fillPositionDefenseStatList(npca, FIRST_BASE_RATING_SHORT, FIRST_BASE_DEFENSE_RATING, result, DEFENSE_1B);

		fillPositionDefenseStatList(npca, SECOND_BASE_RATING_SHORT, SECOND_BASE_DEFENSE_RATING, result, DEFENSE_2B);

		fillPositionDefenseStatList(npca, THIRD_BASE_RATING_SHORT, THIRD_BASE_DEFENSE_RATING, result, DEFENSE_3B);

		fillPositionDefenseStatList(npca, SHORTSTOP_RATING_SHORT, SHORTSTOP_DEFENSE_RATING, result, DEFENSE_SS);

		fillPositionDefenseStatList(npca, OUTFIELD_RATING_SHORT, OUTFIELD_DEFENSE_RATING, result, DEFENSE_OF);

		// Filter stats for detailed card
		APBAHelper.filterStats(result, Arrays.asList(PITCHER_STAT, PITCHER_GAME, BATTER_STAT, BATTER_GAME,
				DEFENSE_TOTAL, DEFENSE_P, DEFENSE_C, DEFENSE_1B, DEFENSE_2B, DEFENSE_3B, DEFENSE_SS, DEFENSE_OF), null,
				Arrays.asList(ERA_LABEL, PITCHER_ERA));

		return result;
	}

	private void fillPositionDefenseStatList(Avatar npca, String statToCheck, int compareTo, Map<String, Object> result, String sublistName) {
		String prefix;
		switch (statToCheck) {
			case FIRST_BASE_RATING_SHORT:
				prefix = FIRST_BASE_PREFIX;
				break;
			case SECOND_BASE_RATING_SHORT:
				prefix = SECOND_BASE_PREFIX;
				break;
			case THIRD_BASE_RATING_SHORT:
				prefix = THIRD_BASE_PREFIX;
				break;
			default:
				prefix = statToCheck.substring(0, statToCheck.length() - 1);
		}
		
		String stat = avatarRepository.getStat(npca, statToCheck);
		if (PHPHelper.toInt(stat) > compareTo) {
			Map<String, Object> map = new LinkedHashMap<>();
			result.put(sublistName, map);
			for (String bs : POSITION_DEFENCE_STAT_MAP.keySet()) {
				String bn = POSITION_DEFENCE_STAT_MAP.get(bs);
				map.put(bn, avatarRepository.getStat(npca, prefix + UNDERSCORE + bs));
			}
		}
	}

	@Transactional(readOnly = true)
	public Lineup getLineupforPitcher(int gameId, int playerId) throws IOException {
		Lineup lineup = new Lineup();
		GameLineup lineupJson = gameLineupRepository.findOneByGameAndPlayer(gameId, playerId);
		List<Map<Integer, Map<String, Object>>> lineupList = JSONUtil.toObject(lineupJson.getLineup());
		lineup.setLineup_list(lineupList);
		return lineup;
	}
	
	@Transactional(readOnly = true)
	public Lineup getLineup(HttpSession session, int gameId, int playerId, String lixarToken, String playerabbr) throws IOException {
		Lineup lineup = new Lineup();
		Avatar pitcher_avatar;
		GameReady gameready = gameReadyRepository.findOneByGame(gameId);
        GameNpc pitcher = gameready.getHomePitcher();
		String hitter = GameConstants.RULE_ID_HITTER_HOME;
		
		if (Boolean.TRUE.equals(session.getAttribute(SELECTING_AWAY))) {
			pitcher = gameready.getAwayPitcher();
			hitter = GameConstants.RULE_ID_HITTER_AWAY;
		}
		
        pitcher_avatar = pitcher.getAvatar();
        Map<String, Object> np = new LinkedHashMap<>();
        np.put(NAME_LABEL, pitcher.getFullNameReversed());
        np.put(ID_LABEL, pitcher.getId());
        np.put(BENCHED, pitcher.isBenched());
        np.put(THROWS_LABEL_SHORT, avatarRepository.getStat(pitcher_avatar, CD_THROWS));
        np.put(GRADE_LABEL_SHORT, avatarRepository.getRes(pitcher_avatar, PITCHER_GRADE_LABEL));
        np.put(CTRL_LABEL, avatarRepository.getStat(pitcher_avatar, CD_STRIKEOUT) + avatarRepository.getStat(pitcher_avatar, CD_CONTROL));
        np.put(EARNED_RUN_AVERAGE, avatarRepository.getStat(pitcher_avatar, SP_ERA));
        np.put(POS_LABEL.toUpperCase(), avatarRepository.getStat(pitcher_avatar, CD_POS));
        np.put(D_LABEL.toUpperCase(), avatarRepository.getStat(pitcher_avatar, CD_POSR));
        np.put(BATS_LABEL_SHORT, avatarRepository.getStat(pitcher_avatar, CD_BATS));
        np.put(SP_LABEL, avatarRepository.getStat(pitcher_avatar, CD_SR));
        np.put(AVG_LABEL.toUpperCase(), avatarRepository.getStat(pitcher_avatar, S_BA));
        np.put(OBP_LABEL, avatarRepository.getStat(pitcher_avatar, S_OBP));
        np.put(SLG_LABEL, avatarRepository.getStat(pitcher_avatar, S_SLG));

        if (avatarRepository.getStat(pitcher_avatar, CD_POS).equals(POSITION_RELIEF_PITCHER)) {
            np.put(GRADE_LABEL_SHORT, np.get(GRADE_LABEL_SHORT) + ASTERISK);
        }
        
        String grade = npcService.convertPitcherGrade(PHPHelper.toInt((String) np.get(GRADE_LABEL_SHORT)));
        np.put(GRADE_LABEL_SHORT, grade == null ? PITCHER_GRADE_D : grade);

        lineup.setPitcher(np);

		APBAHelper.filterStats(lineup.getPitcher(), null, Arrays.asList(AVG_LABEL.toUpperCase(), OBP_LABEL, SLG_LABEL), null);

		GameLineup lineup_json = gameLineupRepository.findOneByGameAndPlayer(gameId, playerId);
		List<Map<Integer, Map<String, Object>>> lineup_list = JSONUtil.toObject(lineup_json.getLineup());
		lineup.setLineup_list(lineup_list);

		List<GameNpc> players = gameNpcRepository.findByPlayerId(playerId);

		GameStat stat = gameRepository.loadStat(gameId, hitter);
		if (PHPHelper.toInt(stat.getStart()) > 0)
			lineup.setLimit(11);
		else
			lineup.setLimit(10);
		
		int teamId = -1;

		List<Map<String, Object>> playerLineup = new ArrayList<>();
		List<Map<String, Object>> playerList = new ArrayList<>();
		for (GameNpc player : players) {
			if (player.getAvatar() == null)
				continue;

			np = new LinkedHashMap<>();
			np.put(NAME_LABEL, player.getFullNameReversed());
			np.put(ID_LABEL, player.getId());
			np.put(CARD_URL_, UrlFactory.generatePlayerCardUrl(lixarToken, (int) np.get(ID_LABEL)));
	        np.put(BENCHED, player.isBenched());
			Avatar playerAvatar = player.getAvatar();
			if (playerAvatar.getId().intValue() != pitcher_avatar.getId().intValue()) {
				np.put(POS_LABEL.toUpperCase(), avatarRepository.getStat(playerAvatar, CD_POS));
				np.put(D_LABEL.toUpperCase(), avatarRepository.getStat(playerAvatar, CD_POSR));
				np.put(BATS_LABEL_SHORT, avatarRepository.getStat(playerAvatar, CD_BATS));
				np.put(SP_LABEL, avatarRepository.getStat(playerAvatar, CD_SR));
				np.put(AVG_LABEL.toUpperCase(), avatarRepository.getStat(playerAvatar, S_BA));
				np.put(OBP_LABEL, avatarRepository.getStat(playerAvatar, S_OBP));
				np.put(SLG_LABEL, avatarRepository.getStat(playerAvatar, S_SLG));
				np.put(STEAL_LABEL.toUpperCase(), StringUtil.substr(avatarRepository.getStat(playerAvatar, CD_ST), 0, 1) + avatarRepository.getStat(playerAvatar, CD_SSN));
				String epos = avatarRepository.getStat(playerAvatar, CD_EPOS);
				if (epos.equals(MISSING_STAT)) {
					np.put(ELIGIBLE_LABEL, PHPHelper.toInt(MISSING_STAT));
					np.put(MULTIPLE_LABEL, 1);
				} else {
					Map<String, Object> eposObject = JSONUtil.toObject(avatarRepository.getStat(playerAvatar, CD_EPOS));
					np.put(ELIGIBLE_LABEL, eposObject);
					np.put(MULTIPLE_LABEL, eposObject.size());
				}
				APBAHelper.filterStats(np, null, Arrays.asList(AVG_LABEL.toUpperCase(), OBP_LABEL, SLG_LABEL), null);
				playerList.add(np);
			}
		}

		playerList.sort((a, b) -> {
			int aD = PHPHelper.toInt((String) a.get(D_LABEL.toUpperCase()));
			int bD = PHPHelper.toInt((String) b.get(D_LABEL.toUpperCase()));
			if (aD < bD)
				return 1;
			if (aD > bD)
				return -1;
			return ((String) a.get(NAME_LABEL)).compareTo((String) b.get(NAME_LABEL));
		});

		Map<String, String> default_ratings = new LinkedHashMap<>();
		default_ratings.put(POSITION_ABBR_CATCHER, CATCHER_DEF_RATING);
		default_ratings.put(POSITION_ABBR_FIRST_BASE, FIRST_BASE_DEF_RATING);
		default_ratings.put(POSITION_ABBR_SECOND_BASE, SECOND_BASE_DEF_RATING);
		default_ratings.put(POSITION_ABBR_THIRD_BASE, THIRD_BASE_DEF_RATING);
		default_ratings.put(POSITION_ABBR_SHORTSTOP, SHORTSTOP_DEF_RATING);
		default_ratings.put(POSITION_ABBR_LEFT_FIELD, OUTFIELD_DEF_RATING);
		default_ratings.put(POSITION_ABBR_RIGHT_FIELD, OUTFIELD_DEF_RATING);
		default_ratings.put(POSITION_ABBR_CENTER_FIELD, OUTFIELD_DEF_RATING);
		lineup.setDefaults(default_ratings);

		lineup.setDh(gameRepository.getStat(gameId, hitter));
		lineup.setTeam_name(playerabbr);
		lineup.setPlayers(playerList);
		lineup.setLineupId(teamId);
		lineup.setLineup(playerLineup);
		lineup.setCount(playerList.size());
		lineup.setOpposingPitcherUrl(UrlFactory.generateGetPitcherUrl(lixarToken));
		lineup.setCardUrl(UrlFactory.generatePlayerCardUrl(lixarToken, 0));
		lineup.setDetailUrl(UrlFactory.generateDetailedUrl(lixarToken, 0));

		return lineup;
	}

	@Transactional(readOnly = true)
	public Map<String, Object> checkGameReadyAction(Integer game, String token) {
		Map<String, Object> result = null;
		GameReady gameready = gameReadyRepository.findOneByGame(game);

		//Conversion note: added for safety
		if (gameready == null) {
			return result;
		}

		if ((Boolean.TRUE.equals(gameready.getHomeLineup())) && (Boolean.TRUE.equals(gameready.getAwayLineup()))) {
			result = new LinkedHashMap<String, Object>();
			//Conversion note: not used, client already knows the url
			result.put(URL_LABEL, UrlFactory.generateGameUrl(token));
		}

		return result;
	}

	@Transactional
	public void resumeAction(Integer game, Boolean playerHome, Integer player, String token) {
		Game game_obj = gameRepository.findById(game).get();
		if (playerHome) {
			game_obj.setReady(game_obj.getReady() & 12);
		} else {
			game_obj.setReady(game_obj.getReady() & 3);
		}
		//Update player activity when boxscore or substitution menu is closed
		APBASession player_session = sessionRepository.findOneByGameAndPlayer(game, player);
		player_session.setLastactionToNow();
		//Conversion note: markStatusChanged moved to caller to separate transactions
	}

	@Transactional
	public Object windowTokenAction(Integer game, Integer player, String token) {

		APBASession player_session = sessionRepository.findOneByGameAndPlayer(game, player);
		String[] lt = token.split("\\|");

		if (!player_session.getGametoken().equals(lt[0])) {
			Map<String, String> result = new HashMap<String, String>();
			result.put(ERROR_LABEL, SESSION_INVALID);
			return result;
		}

		//Conversion note: getting non-existing array element in PHP will return null
		if (lt.length == 1) {
			player_session.setToken(null);
		} else {
			player_session.setToken(lt[1]);
		}
		player_session.setLastactionToNow();
		//Conversion note: markStatusChanged moved to caller to separate transactions
		//null means method executed successfully
		return null;
	}

	@Transactional
	public Map<String, Object> nonSwingAction(HttpSession session, String action, String value) throws IOException {
		//Note: doing setUp here to prevent detached state of gr
		Map<String, Object> game = gameService.setUp(session);
		GameReady gr = (GameReady) game.get(GAME);

		if (HOLD_R1.equals(action)) {
			gr.setR1Held(value);
		} else if (HOLD_R3.equals(action)) {
			gr.setR3Held(value);
		} else if (INFIELD_LABEL.equals(action)) {
			gr.setGameInfield(value);
		} else if (PLAYESAFE_R1.equals(action)) {
			//Conversion note: value for playsafe_r* actions are "1" or "0"
			gr.setPlaySafeR1(INT_STR_ONE.equals(value) ? true : false);
		} else if (PLAYESAFE_R2.equals(action)) {
			gr.setPlaySafeR2(INT_STR_ONE.equals(value) ? true : false);
		} else if (PLAYESAFE_R3.equals(action)) {
			gr.setPlaySafeR3(INT_STR_ONE.equals(value) ? true : false);
		} else if (STRETCH_LABEL.equals(action)) {
			gr.setStretch(value);
		} else if (PITCH_LABEL.equals(action)) {
			gr.setPitch(true);
		} else if (CONFIRM_LABEL.equals(action)) {
			gr.deleteAdviceReplacementMarks((Boolean) session.getAttribute(SessionKeys.PLAYER_HOME));
		}

        Map<String, Object> response = null;
        if(!isAutoplayGame(gr)) {
            response = gameService.response(game, session);
        }
		return response;
	}

	@Transactional
	public Map<String, Object> forfeitAction(HttpSession session, String abort) throws IOException {
		Map<String, Object> game = gameService.setUp(session);
		GameReady gameready = (GameReady) game.get(GAME);
		if (INT_STR_ONE.equals(abort)) {
			gameready.setForfeit(GameService.GAME_RESULT_FORFEITED);
		} else if ((boolean) session.getAttribute(SessionKeys.PLAYER_HOME)) {
			gameready.setForfeit(GameService.GAME_RESULT_FORFEITED_BY_HOME);
		} else {
			gameready.setForfeit(GameService.GAME_RESULT_FORFEITED_BY_AWAY);
		}
		endGameService.finishGame(gameready, Boolean.TRUE.equals(session.getAttribute(SessionKeys.AI)));

		entityManager.persist(gameready);
		
		if (!AutoplayUtil.isAutoplayGame((Integer) session.getAttribute(GAME))) {
			gameStatusService.markStatusChanged(session, false);
		}
		
		entityManagerUtil.flush(session);

		game.put(GAME, gameready);

		return gameService.response(game, session);
	}

	@Transactional
	public Map<String, Object> substitutionAction(HttpSession session, String lineupData) throws IOException {

		Map<String, Object> game = gameService.setUp(session);

		String[] strdata = PHPHelper.explode(VERTICAL_BAR_CHAR, lineupData);

		Map<String, String> data = new LinkedHashMap<>();
		Map<String, String> batting = new LinkedHashMap<>();

		for (String pos : strdata) {
			String[] x = PHPHelper.explode(COMMA_CHAR, pos);
			if (x.length == 3) {
				data.put(x[0], x[1]);
				batting.put(x[0], x[2]);
			}
		}
		if (!PHPHelper.is_numeric(batting.get(POSITION_PITCHER_SHORT))) {
			batting.put(POSITION_PITCHER_SHORT, INT_STR_ZERO);
		}

		//Conversion note: var playerhome to reduce code size
		boolean playerhome = (boolean) session.getAttribute(SessionKeys.PLAYER_HOME);
		boolean isSolitaire = APBAHelper.isSolitaireGame(session);
		if (isSolitaire) {
			playerhome = (boolean) session.getAttribute(SessionKeys.PLAYING_HOME);
		}

		GameReady gameready = (GameReady) game.get(GAME);
		boolean isDefense = true;

		//player is on offense
		if (gameready.getInningTop() != playerhome) {
			isDefense = false;
		}

		int inning = gameready.getInningTop() ? gameready.getInning() - 1 : gameready.getInning();
		Integer playerId = (Integer) session.getAttribute(SessionKeys.PLAYER);
		if (isSolitaire && !playerhome) {
			ExternalGame externalGame = externalGameRepository.findOneByIntGid(gameready.getGame().getId());
			playerId = externalGame.getExternalAwayPlayerId();
		}
		List<Map<String, Object>> substitutions = gameSubstitutionRepository.subsByPinch((Integer) session.getAttribute(SessionKeys.GAME), playerId, inning);
		//Conversion note: substitutions used as set of "to" fields
		Set<Integer> substitutionsTo = new HashSet<>();
		if (substitutions.size() > 0) {
			substitutionsTo = substitutions.stream().map(e -> (Integer) e.get("to")).collect(Collectors.toSet());
		}


		gameready.setPitch(false);

		int team = 0;
		int outfield = 0;

		for (String pos : DEFENCE_POSITIONS) {
			int rating = swapIfNeed(game, gameready, session, pos, data, batting, substitutionsTo, isDefense, playerhome);
			switch (pos) {
				case POSITION_LEFT_FIELD:
				case POSITION_CENTER_FIELD:
				case POSITION_RIGHT_FIELD:
					outfield += rating;
					break;
				default:
					team += rating;
			}
		}

		if (data.containsKey(POSITION_DESIGNATED_HITTER)) {
			GameNpc npc = gameready.getNpcByPositionAndInningPart(HITTER_LABEL, playerhome);
			Integer id = npc.getId();
			if (id != PHPHelper.toInt(data.get(POSITION_DESIGNATED_HITTER))) {
				npcService.swapPlayer(game, npc, gameNpcRepository.findOneById(PHPHelper.toInt(data.get(POSITION_DESIGNATED_HITTER))), POSITION_DESIGNATED_HITTER, session, gameready, batting.get(POSITION_DESIGNATED_HITTER), playerhome);
			}
		}

		//Update substitutions - may be PH/PR players was already substituted
		substitutions = gameSubstitutionRepository.subsByPinch((Integer) session.getAttribute(SessionKeys.GAME), playerId, inning);
		//player has no PH/PR
		if ((gameready.getInningTop() == playerhome) && substitutions.size() == 0) {
			gameready.deleteAdviceReplacementMarks(playerhome);
		}

		entityManager.persist(gameready);
		GameNpc team_npc = gameNpcRepository.findByPlayerIdAndLastName(playerId, TEAM_LABEL);
		GameNpc infield_npc = gameNpcRepository.findByPlayerIdAndLastName(playerId, INFIELD_LABEL);

		GameNpcStat stat;
		if (team >= 35) {
			stat = gameNpcRepository.setStat(infield_npc, RATING_LABEL, INT_STR_ONE);
		} else if (team < 30) {
			stat = gameNpcRepository.setStat(infield_npc, RATING_LABEL, INT_STR_THREE);
		} else {
			stat = gameNpcRepository.setStat(infield_npc, RATING_LABEL, INT_STR_TWO);
		}
		
		entityManager.persist(stat);

		team += outfield;

		if (team >= 41) {
			stat = gameNpcRepository.setStat(team_npc, RATING_LABEL, INT_STR_ONE);
		} else if (team < 36) {
			stat = gameNpcRepository.setStat(team_npc, RATING_LABEL, INT_STR_THREE);
		} else {
			stat = gameNpcRepository.setStat(team_npc, RATING_LABEL, INT_STR_TWO);
		}
		
		entityManager.persist(stat);
		
		if (!isSolitaire) {
			gameStatusService.markStatusChanged(session, false);
		}
	
        Map<String, Object> response = null;
        if(!isAutoplayGame(gameready)) {
        	game.put(GAME, gameready);
    		game = gameService.setUp(session);
            response = gameService.response(game, session);
        }
		return response;
	}

	/**
	 * Substitute player at specified position if he was checked for substitution.
	 * @return old or new player rating
	 */
	private int swapIfNeed(Map<String, Object> game, GameReady gameready, HttpSession session, String positionAbbr, Map<String, String> data, Map<String, String> batting, Set<Integer> substitutionsTo, boolean isDefense, boolean playerHome) {
		GameNpc npc = gameready.getNpcByPositionAndInningPart(GameService.translate(positionAbbr), playerHome);
		if (needToSwap(data, positionAbbr, npc.getId(), substitutionsTo, npc.getAvatar().getId(), isDefense)) {
			if (!isDefense) {
				if(POSITION_PITCHER_SHORT.equals(positionAbbr)) {
					gameready.addReplacementMark(playerHome, GameReady.REPLACEMENT_ADVICE_PITCHER);
				} else {
					gameready.addReplacementMark(playerHome, GameReady.REPLACEMENT_ADVICE);
				}
			}
			return PHPHelper.toInt(npcService.swapPlayer(game, npc, gameNpcRepository.findOneById(PHPHelper.toInt(data.get(positionAbbr))), positionAbbr, session, gameready, batting.get(positionAbbr), playerHome));
		} else {
			return PHPHelper.toInt(gameNpcRepository.getStat(npc, RATING_LABEL));
		}
	}

	private boolean needToSwap(Map<String, String> data, String positionAbbr, Integer npcId, Set<Integer> substitutionsTo, Integer avatarId, boolean isDefense) {
		return data.containsKey(positionAbbr) && (npcId != PHPHelper.toInt(data.get(positionAbbr)) || wasPinchPlayer(substitutionsTo, avatarId, isDefense));
	}

	/**
	 * True if user wants to leave PH/PR for defense
	 */
	private boolean wasPinchPlayer(Set<Integer> subsTo, Integer avatarId, boolean isDefense) {
		return isDefense && subsTo.contains(avatarId);
	}

	@Transactional(readOnly = true)
	public Map<String, Object> getAvailableSubsAction(int playerId) {
		List<GameNpc> players = gameNpcRepository.findByPlayerId(playerId);

		List<Map<String, Object>> pitcherList = new ArrayList<>();
		List<Map<String, Object>> playerList = new ArrayList<>();
		for (GameNpc playerNpc : players) {
			boolean playerIsInitialized = false;
			Avatar npca = playerNpc.getAvatar();
			avatarRepository.makeResExpire(npca.getId());
			if (INFIELD_LABEL.equals(playerNpc.getLastName()) || TEAM_LABEL.equals(playerNpc.getLastName())) {
				continue;
			}

			Map<String, Object> np = new LinkedHashMap<>();
			if (PHPHelper.toInt(avatarRepository.getCachedRes(npca, RELIEF_PITCHER_GRADE_LABEL_LONG)) > 0 || 
				PHPHelper.isTrue(avatarRepository.getCachedRes(npca, PITCHER_GRADE_LABEL))) {
				
				playerIsInitialized = initializePlayer(np, playerNpc);
				
				np.put(THROWS_LABEL_SHORT, avatarRepository.getStat(npca, CD_THROWS));
				np.put(GRADE_LABEL_SHORT, avatarRepository.getCachedRes(npca, RELIEF_PITCHER_GRADE_LABEL_LONG));

				if (PHPHelper.toInt((String) np.get(GRADE_LABEL_SHORT)) == 0) {
					np.put(GRADE_LABEL_SHORT, avatarRepository.getCachedRes(npca, PITCHER_GRADE_LABEL));
				}

				np.put(PITCHER_GRADE_LABEL, PHPHelper.toInt(avatarRepository.getCachedRes(npca, PITCHER_GRADE_LABEL)));
				np.put(RELIEF_PITCHER_GRADE_LABEL_SHORT, PHPHelper.toInt(avatarRepository.getCachedRes(npca, RELIEF_PITCHER_GRADE_LABEL_LONG)));

				np.put(CTRL_LABEL, avatarRepository.getStat(npca, CD_STRIKEOUT) + avatarRepository.getStat(npca, CD_CONTROL));
				np.put(EARNED_RUN_AVERAGE, avatarRepository.getStat(npca, SP_ERA));

				if (((BigDecimal) np.get(AVG_LABEL.toUpperCase())).compareTo(BigDecimal.ONE) == 0) {
					np.put(AVG_LABEL.toUpperCase(), DEFAULT_AVG);
				} else {
					// Conversion note: pulled logic out into a variable
					BigDecimal avg = (BigDecimal) np.get(AVG_LABEL.toUpperCase());
					avg = avg.multiply(new BigDecimal(1000));
					avg = PHPHelper.round(avg, 0);
					np.put(AVG_LABEL.toUpperCase(), PERIOD + avg);
				}
				
				String grade = npcService.convertPitcherGrade(PHPHelper.toInt((String) np.get(GRADE_LABEL_SHORT)));
				np.put(GRADE_LABEL_LONG, grade == null ? PITCHER_GRADE_D : grade);
				np.put(BENCHED, playerNpc.isBenched());
				np.put(W_LABEL, avatarRepository.getStat(npca, SP_W));
				np.put(L_LABEL, avatarRepository.getStat(npca, SP_L));
				np.put(SV_LABEL, avatarRepository.getStat(npca, SP_SV));
				np.put(RATING_LABEL.toUpperCase(), avatarRepository.getStat(npca, CD_RATING));
				np.put(STAT_REF_KEY_IP.toUpperCase(), avatarRepository.getStat(npca, SP_IP));
				
				// Filter stats for available pitchers
				APBAHelper.filterStats(np, new ArrayList<>(), Arrays.asList(H_AVG_LABEL, OBP_LABEL, SLG_LABEL, STAT_REF_KEY_IP), null);
				pitcherList.add(np);
			}
			// Some of this is repetitive - could reduce code and database calls significantly
			if (!playerNpc.isBenched()) {
				if (!playerIsInitialized) {
					initializePlayer(np, playerNpc);
				}
				
				Map<String, Object> eposObject = JSONUtil.toObjectSafe(avatarRepository.getStat(npca, CD_EPOS), log);
				np.put(ELIGIBLE_LABEL, eposObject);
				np.put(MULTIPLE_LABEL, eposObject == null ? 0 : eposObject.size());

				// Filter stats for available players
				APBAHelper.filterStats(np, new ArrayList<>(), Arrays.asList(H_AVG_LABEL, OBP_LABEL, SLG_LABEL, STAT_REF_KEY_IP), null);
				playerList.add(np);
			}
		}

		Collections.sort(pitcherList, (a, b) -> {
			int arpg = PHPHelper.toInt((Integer) a.get(RELIEF_PITCHER_GRADE_LABEL_SHORT));
			int brpg = PHPHelper.toInt((Integer) b.get(RELIEF_PITCHER_GRADE_LABEL_SHORT));
			if (arpg == NO_PITCHER_GRADE && brpg != NO_PITCHER_GRADE)
				return 1;
			if (arpg != NO_PITCHER_GRADE && brpg == NO_PITCHER_GRADE)
				return -1;
			if (arpg < brpg)
				return 1;
			if (arpg > brpg)
				return -1;
			int apg = PHPHelper.toInt((Integer) a.get(PITCHER_GRADE_LABEL));
			int bpg = PHPHelper.toInt((Integer) b.get(PITCHER_GRADE_LABEL));
			if (apg < bpg && arpg == NO_PITCHER_GRADE && brpg == NO_PITCHER_GRADE)
				return 1;
			if (apg > bpg && arpg == NO_PITCHER_GRADE && brpg == NO_PITCHER_GRADE)
				return -1;
			// Conversion Note Simplified to using string compare
			return ((String) a.get(NAME_LABEL)).compareTo((String) b.get(NAME_LABEL));
		});
		Collections.sort(playerList, (a, b) -> {
			int aD = PHPHelper.toInt((String) a.get(PITCHER_GRADE_D));
			int bD = PHPHelper.toInt((String) b.get(PITCHER_GRADE_D));
			if (aD < bD)
				return 1;
			if (aD > bD)
				return -1;
			// Conversion Note Simplified to using string compare
			return ((String) a.get(NAME_LABEL)).compareTo((String) b.get(NAME_LABEL));
		});

		Map<String, Object> data = new LinkedHashMap<>();
		data.put(PITCHER_LIST_LABEL, pitcherList);
		data.put(PLAYERS_LABEL, playerList);
		data.put(DEFAULTS_LABEL, DEFAULTS);
		return data;
	}
	
	public boolean initializePlayer(Map<String, Object> player, GameNpc playerNpc) {
		Avatar npca = playerNpc.getAvatar();
		player.put(NAME_LABEL, playerNpc.getLastName());
        player.put(ID_LABEL, playerNpc.getId());
        player.put(POS_LABEL.toUpperCase(), avatarRepository.getStat(npca, CD_POS));
        player.put(D_LABEL.toUpperCase(), avatarRepository.getStat(npca, CD_POSR));
        player.put(BATS_LABEL_SHORT, avatarRepository.getStat(npca, CD_BATS));
        player.put(SP_LABEL, avatarRepository.getStat(npca, CD_SR));
        player.put(H_AVG_LABEL, avatarRepository.getStat(npca, S_BA));
        player.put(OBP_LABEL, avatarRepository.getStat(npca, S_OBP));
        player.put(SLG_LABEL, avatarRepository.getStat(npca, S_SLG));
        player.put(STEAL_LABEL.toUpperCase(), StringUtil.substr(avatarRepository.getStat(npca, CD_ST), 0, 1) + avatarRepository.getStat(npca, CD_SSN));
        // Conversion note - this used to be npca which was wrong - but does not appear to be used - so removed
        // np.put(BATTING_LABEL, gameNpcRepository.loadStat(player_npc, BATTING_LABEL));
        player.put(POSITION_ATBAT, PHPHelper.max(0, avatarRepository.getResBigDecimalOrZero(npca, ATBAT)));
        player.put(H_LABEL, PHPHelper.max(0, avatarRepository.getResBigDecimalOrZero(npca, SINGLE).add(avatarRepository.getResBigDecimalOrZero(npca, DOUBLE)).add(avatarRepository.getResBigDecimalOrZero(npca, TRIPLE)).add(avatarRepository.getResBigDecimalOrZero(npca, HOMERUN))));
        player.put(AVG_LABEL.toUpperCase(), PHPHelper.round(((BigDecimal) player.get(H_LABEL)).divide(PHPHelper.max(1, (BigDecimal) player.get(POSITION_ATBAT)), DEFAULT_CONTEXT), 3));
        player.put(REST_LABEL, avatarRepository.getStat(npca, CD_FATIGUE_LEVEL));
        
        if (PHPHelper.isTrue(avatarRepository.getCachedRes(npca, INJURY)))
            player.put(AVAILABLE, INJURY_LABEL);
        else if (PHPHelper.isTrue(avatarRepository.getCachedRes(npca, EJECTED_LABEL)))
            player.put(AVAILABLE, EJECTED_LABEL);
        else if (PHPHelper.isNotEqualToZero(gameNpcRepository.getStat(playerNpc, POSITION_LABEL)))
            player.put(AVAILABLE, PLAYED);
        else if (playerNpc.isBenched()) 
            player.put(AVAILABLE, BENCHED.toLowerCase());
        else
            player.put(AVAILABLE, AVAILABLE);

        player.put(INJURY_LABEL, avatarRepository.getCachedRes(npca, INJURY));
        player.put(EJECTED_LABEL, avatarRepository.getCachedRes(npca, EJECTED_LABEL));
        player.put(PLAYING_LABEL, gameNpcRepository.getStat(playerNpc, POSITION_LABEL));
        
        return true;
	}

	@Transactional(readOnly = true)
	public Map<String, Object> getConsoleAction(int gameId, int level) {
		List<GameConsole> console_data = gameConsoleRepository.getAllMessages(gameId, level);
		Map<String, Object> console = new LinkedHashMap<>();
		for (GameConsole d : console_data) {
			console.put(d.getId().toString(), d.getMessage());
		}
		return console;
	}

	@Transactional
	public Object gameStatusAction(HttpSession session, String token) throws IOException, NotFoundException {
		Map<String, Object> game = gameService.setUp(session);
		if(game == null) {
			return null;
		}
		
		GameReady gameready = (GameReady) game.get(GAME);
		if (!isAutoplayGame(gameready)) {
            Map<String, Object> check = gameService.checkSessionWithJSONReturn(session, token, false);
            if (check.size() > 0) {
                return check;
            }
        }

		Boolean ai = (Boolean) session.getAttribute(AI);
        boolean hasAI = ai != null && (ai || isAutoplayGame(gameready));

        if(hasAI && (boolean) session.getAttribute(OPPONENT_HOME) && gameready.getInningTop() && !gameready.getPitch()) {
            generateAiPitching(session, game, gameready);
        }
        if(hasAI && (boolean) session.getAttribute(OPPONENT_HOME) && !gameready.getInningTop() && gameready.getPitch()) {
            return generateAIBatting(session, token, game, gameready);
        }
        if(hasAI && (boolean) session.getAttribute(PLAYER_HOME) && gameready.getInningTop() && gameready.getPitch()) {
            return generateAIBatting(session, token, game, gameready);
        }
        if(hasAI && (boolean) session.getAttribute(PLAYER_HOME) && !gameready.getInningTop() && !gameready.getPitch()) {
            generateAiPitching(session, game, gameready);
        }
		if (isAutoplayGame(gameready) && gameready.getPitch()) {
            return generateAIBatting(session, token, game, gameready);
        }
        if (isAutoplayGame(gameready) && !gameready.getPitch()) {
            generateAiPitching(session, game, gameready);
        }
        
        if (APBAHelper.isPlayerVsAiGame(session)) {
        	entityManagerUtil.flushDefaultTickInterval(session);
        }

        Map<String, Object> response = null;
        if(!isAutoplayGame(gameready))
            response = gameService.response(game, session);
        if (gameready.getGameActive() && isAutoplayGame(gameready)) {
            swapSessionPlayers(session, gameready);
        }
        return response;
	}

    private void generateAiPitching(HttpSession session, Map<String, Object> game, GameReady gameready) {
        microManagerFactory.getMicroManagerService(APBAHelper.getMicroManagerId(gameready, session)).newBatter(session, game);
        gameready.setPitch(true);
        entityManager.persist(gameready);
        if (!AutoplayUtil.isAutoplayGame((Integer) session.getAttribute(GAME))) {
        	gameStatusService.markStatusChanged(session, false);
        }
    }

    private Map<String, Object> generateAIBatting(HttpSession session, String token, Map<String, Object> game, GameReady gameready) throws IOException, NotFoundException {
        String hit = microManagerFactory.getMicroManagerService(APBAHelper.getMicroManagerId(gameready, session)).batting(session, game);
        Dice dice = avatarDiceRepository.findByAvatarAndPriority(((GameNpc) game.get(BATTER_LABEL)).getAvatar(), 1).getDice();
        Map<String, Object> response = apbaDiceService.rollAction(session, 100, dice.getSid(), hit, token, null);
        
        if (response.get(GAME).equals(PLAYING_LABEL) && isAutoplayGame(gameready)) {
            swapSessionPlayers(session, gameready);
            gameStatusAction(session, token);
        }
	
		if (!gameready.getGameActive() && isAutoplayGame(gameready) && (int) session.getAttribute(PLAYER) != (int) session.getAttribute(HOME_AI_ID)) {
			//Ensure home team is actually home team at end of game.
			swapSessionPlayers(session, gameready);
		}
		
        return response;
    }

    private void swapSessionPlayers(HttpSession session, GameReady gameReady) {
        Side playerSide, opponentSide;
        int player, playerId, opponent, opponentId; 
        
        if (session.getAttribute(PLAYER).equals((int) session.getAttribute(PLAYER))) {
        	playerSide = Side.AWAY;
        	opponentSide = Side.HOME;

            playerId = (int) session.getAttribute(OPPONENT_ID_LABEL);
            opponentId = (int) session.getAttribute(PLAYERID_LABEL);
            player = (int) session.getAttribute(OPPONENT);
            opponent = (int) session.getAttribute(PLAYER);
        } else {
        	playerSide = Side.HOME;
        	opponentSide = Side.AWAY;

            playerId = (int) session.getAttribute(PLAYERID_LABEL);
            opponentId = (int) session.getAttribute(OPPONENT_ID_LABEL);
            player = (int) session.getAttribute(PLAYER);
            opponent = (int) session.getAttribute(OPPONENT);
        }
        
        session.setAttribute(OPPONENT, opponent);
        session.setAttribute(OPPONENT_ID, opponentId);
        session.setAttribute(OPPONENT_TEAM, gameReady.getTeam(opponentSide));
        session.setAttribute(OPPONENT_ABBR, gameReady.getAbbr(opponentSide));
        session.setAttribute(OPPONENT_HOME, opponentSide == Side.HOME);

        session.setAttribute(PLAYER, player);
        session.setAttribute(PLAYER_ID, playerId);
        session.setAttribute(PLAYER_TEAM, gameReady.getTeam(playerSide));
        session.setAttribute(PLAYER_ABBR, gameReady.getAbbr(playerSide));
        session.setAttribute(PLAYER_HOME, playerSide == Side.HOME);
    }

    private void swapSessionValue(HttpSession session, String sourceKey, String destKey) {
	    Object source = session.getAttribute(sourceKey);
	    session.setAttribute(sourceKey, session.getAttribute(destKey));
	    session.setAttribute(destKey, source);
    }

	@Transactional
	public void setGameReady(Integer gameId, boolean playerHome, int playerMask, int opponentMask) {
		Game game_obj = gameRepository.findById(gameId).get();
		if (playerHome) {
			game_obj.setReady(PHPHelper.toInt(game_obj.getReady()) | playerMask);
		} else {
			game_obj.setReady(PHPHelper.toInt(game_obj.getReady()) | opponentMask);
		}
	}

    @Transactional
    public void setCurrentInning(HttpSession session, int currentInning){
	    Integer gameId = (Integer) session.getAttribute(SessionKeys.GAME);
	    if (gameId != null) {
            inningsMap.put(gameId, currentInning);
        }
    }
    @Transactional(readOnly = true)
    public Map<String, Object> currentAutoPlayInning(HttpSession session) {
        Map<String, Object> response = new LinkedHashMap<>();
        Integer gameId = (Integer) session.getAttribute(SessionKeys.GAME);
        if (gameId != null) {
            response.put(CURRENT_INNING, inningsMap.get(gameId));
        }
        return response;
    }

	@Transactional
	public void modifyGameResult(Integer gameId) {
		GameReady gameReady = gameReadyRepository.findOneByGame(gameId);

		if (gameReady.getForfeit() == GameService.GAME_RESULT_RAIN) {
			if (gameReady.getInning() > 5) {
				if (gameReady.getHome() != gameReady.getAway()) {
					//game should be closed as if it were a regular full length game
					gameReady.setForfeit(GameService.GAME_RESULT_RAIN_AS_NORMAL);
				} else {
					//neither team gets called the winner but both users get 10 points
					gameReady.setForfeit(GameService.GAME_RESULT_RAIN_DRAW);
				}
				entityManager.persist(gameReady);
			}
		}
	}

	public static class Lineup {
		private Map<String, Object> pitcher;
		private int pitcherId;
		private int limit;
		private int lineupId;
		private List <Map<String, Object>> lineup;
		private List<Map<Integer, Map<String, Object>>> lineup_list;
		private String dh;
		private String team_name;
		private List<Map<String, Object>> players;
		private String opposing_pitcher_url;
		private Map<String, String> defaults;
		private String card_url;
		private String detail_url;
		private int count;

		public Map<String, Object> getPitcher() {
			return pitcher;
		}

		public void setPitcher(Map<String, Object> pitcher) {
			this.pitcher = pitcher;
		}
		
		public int getPitcherId() {
			return pitcherId;
		}
		
		public void setPitcherId(int pitcherId) {
			this.pitcherId = pitcherId;
		}

		public int getLimit() {
			return limit;
		}

		public void setLimit(int limit) {
			this.limit = limit;
		}
		
		public int getLineupId() {
			return lineupId;
		}
		
		public void setLineupId(int lineupId) {
			this.lineupId = lineupId;
		}
		
		public List<Map<String, Object>> getLineup() {
			return lineup;
		}
		
		public void setLineup(List<Map<String, Object>> lineup) {
			this.lineup = lineup;
		}

		public List<Map<Integer, Map<String, Object>>> getLineup_list() {
			return lineup_list;
		}

		public void setLineup_list(List<Map<Integer, Map<String, Object>>> lineup_list) {
			this.lineup_list = lineup_list;
		}

		public String getDh() {
			return dh;
		}

		public void setDh(String dh) {
			this.dh = dh;
		}

		public String getTeam_name() {
			return team_name;
		}

		public void setTeam_name(String team_name) {
			this.team_name = team_name;
		}

		public List<Map<String, Object>> getPlayers() {
			return players;
		}

		public void setPlayers(List<Map<String, Object>> players) {
			this.players = players;
		}

		public String getOpposing_pitcher_url() {
			return opposing_pitcher_url;
		}

		public void setOpposingPitcherUrl(String opposing_pitcher_url) {
			this.opposing_pitcher_url = opposing_pitcher_url;
		}

		public Map<String, String> getDefaults() {
			return defaults;
		}

		public void setDefaults(Map<String, String> defaults) {
			this.defaults = defaults;
		}

		public String getCard_url() {
			return card_url;
		}

		public void setCardUrl(String card_url) {
			this.card_url = card_url;
		}

		public String getDetail_url() {
			return detail_url;
		}

		public void setDetailUrl(String detail_url) {
			this.detail_url = detail_url;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}
	}
}
