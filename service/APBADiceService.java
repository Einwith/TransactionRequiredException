package com.lixar.apba.service;

import static com.lixar.apba.core.util.APBAHelper.isAutoplayGame;
import static com.lixar.apba.core.util.GameConstants.*;
import static com.lixar.apba.web.SessionKeys.*;
import static com.lixar.apba.service.BookService.ASTERISK;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;

import com.lixar.apba.core.util.APBAHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lixar.apba.core.util.PHPHelper;
import com.lixar.apba.domain.APBASession;
import com.lixar.apba.domain.Avatar;
import com.lixar.apba.domain.AvatarRes;
import com.lixar.apba.domain.Dice;
import com.lixar.apba.domain.DiceResult;
import com.lixar.apba.domain.GameNpc;
import com.lixar.apba.domain.GameReady;
import com.lixar.apba.domain.Res;
import com.lixar.apba.repository.ApbaSessionRepository;
import com.lixar.apba.repository.AvatarDiceRepository;
import com.lixar.apba.repository.AvatarRepository;
import com.lixar.apba.repository.DiceRepository;
import com.lixar.apba.repository.DiceResultRepository;
import com.lixar.apba.repository.GameNpcRepository;
import com.lixar.apba.repository.GameRepository;
import com.lixar.apba.repository.ResRepository;
import com.lixar.apba.repository.util.ResRepositoryUtil;
import com.lixar.apba.service.util.ClientUtil;
import com.lixar.apba.service.util.EntityManagerUtil;
import com.lixar.apba.service.util.TimerUtil;
import com.lixar.apba.web.NotFoundException;
import com.lixar.apba.web.ModelConstants.Side;
import com.lixar.apba.web.rest.util.GameConsoleUtil;

@Service
public class APBADiceService {

	@Inject
	private GameService gameService;
	
	@Inject
	private EndGameService endGameService;

	@Inject
	private GameConsoleUtil gameConsoleUtil;

	@Inject
	private ApbaSessionRepository apbaSessionRepository;

	@Inject
	private DiceRepository diceRepository;

	@Inject
	private DiceResultRepository diceResultRepository;

	@Inject
	private AvatarRepository avatarRepository;

	@Inject
	private AvatarDiceRepository avatarDiceRepository;

	@Inject
	private GameNpcRepository gameNpcRepository;

	@Inject
	private GameRepository gameRepository;

	@Inject
	private DieRollService dieRollService;

	@Inject
	private GameStatusService gameStatusService;

	@Inject
	private DiceModifierService diceModifierService;

	@Inject
	private APBAParserService apbaParserService;

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private EntityManagerUtil entityManagerUtil;

	@Transactional
	public Map<String, Object> rollAction(HttpSession session, Integer pool, Integer sid, String hit,
										  String token, String roll) throws NotFoundException, IOException {
		TimerUtil rollActionTimer = new TimerUtil("rollActionTimer");
		Map<String, Object> game = gameService.setUp(session);
		APBASession player_session = apbaSessionRepository.findOneByGameAndPlayer((Integer) session.getAttribute(GAME), (Integer) session.getAttribute(PLAYER));

		Map<String, Object> errorResult = new LinkedHashMap<>();
        String[] lt = token.split("\\|");
		//noinspection StatementWithEmptyBody Conversion Note: there is no body in the php
		if (Boolean.FALSE.equals(session.getAttribute(AI)) && !player_session.getGametoken().equals(lt[0])) {
            errorResult.put(ERROR_LABEL, SESSION_INVALID);
            return errorResult;
		} else if (player_session.getToken() != null) {
			if (lt.length != 2) {
				errorResult.put(ERROR_LABEL, SESSION_INVALID);
				return errorResult;
			} else if (!player_session.getToken().equals(lt[1]) || !player_session.getGametoken().equals(lt[0])) {
				errorResult.put(ERROR_LABEL, SESSION_INVALID);
				return errorResult;
			}
		}

		GameReady gameready = (GameReady) game.get(GAME);

		if(gameready.isBeginningOfInning()) {
			gameready.setBeginningOfInning(false);
		}

		gameready.setBusy(PHPHelper.time() + HYPHEN_LABEL + session.getAttribute(PLAYER));
		entityManager.persist(gameready);

		List<Map<String, Object>> dierolls = new ArrayList<>();
		List<String> parser = new ArrayList<>();

		int sid_mod;
		if (HITRUN_LABEL.equals(hit)) {
			sid_mod = 12;
		} else if (SACRIFICE_LABEL.equals(hit)) {
			sid_mod = 11;
		} else {
			sid_mod = 0;
		}


		/*
		 * Begin rolling.
		 */
		rollActionTimer.endTimer();
		return doRollAction(session, game, 0, pool, sid, dierolls, parser, sid_mod, roll);
	}

	/*
	 * This is the event that does the work. Everything has been initialized, rolls get passed in, it follows through as required.
	 */
	private Map<String, Object> doRollAction(HttpSession session, Map<String, Object> game, int die_id, Integer nullablePool,
											 Integer nullableSid, List<Map<String, Object>> dierolls, List<String> parser, int sid_mod, String roll) throws NotFoundException, IOException {
		TimerUtil doRollActionTimer = new TimerUtil("doRollActionTimer");
		TimerUtil doRollActionTimer0 = new TimerUtil("doRollActionTimer0");
		int pool = PHPHelper.toInt(nullablePool);
		int sid = PHPHelper.toInt(nullableSid);
		List<String> new_parser = new ArrayList<>();

		GameReady gameready = (GameReady) game.get(GAME);
		gameready.setPitch(false);
		//entityManager.persist(gameready);
		

		// Conversion note: not used $console = "-777";
		boolean rolling = true;
		//gameready = (GameReady) game.get(GAME);
		if (PHPHelper.isTrue(gameready.getNextCommand())) {

			gameConsoleUtil.writeToConsole(gameready, SPECIAL_PLAY, MESSAGE_PBP);

			parser.add(gameready.getNextCommand());
			rolling = false;
			gameready.setNextCommand(EMPTY_STRING);
			
		}
		doRollActionTimer0.endTimer();
		TimerUtil doRollActionTimer1 = new TimerUtil("doRollActionTimer1");
		entityManager.persist(gameready);
		game.put(GAME, gameready);
		
		entityManagerUtil.flush(session);
		doRollActionTimer1.endTimer();
		TimerUtil doRollActionTimer2 = new TimerUtil("doRollActionTimer2");
		boolean ruleManualRollDice = PHPHelper.isTrue(gameRepository.getStat(gameready.getGame().getId(), RULE_ID_MANUAL_DICE));
        boolean injuryDice = PHPHelper.isTrue(gameRepository.getStat(gameready.getGame().getId(), RULE_ID_DISABLE_INJURY));
        boolean rainReroll = PHPHelper.isTrue(gameRepository.getStat(gameready.getGame().getId(), RULE_ID_REROLL_RAIN));
        boolean rerollInjury = false;
        doRollActionTimer2.endTimer();
        TimerUtil doRollActionTimer3 = new TimerUtil("doRollActionTimer3");
        GameNpc batterNpc = (GameNpc) game.get(BATTER_LABEL);
        GameNpc pitcher = (GameNpc) game.get(PITCHER_LABEL);
        doRollActionTimer3.endTimer();
        TimerUtil doRollActionTimer4 = new TimerUtil("doRollActionTimer4");
		// Conversion Note: never used
		// String parse_out = EMPTY_STRING;
		if (rolling) {
		   	/*
			 * Roll the die. In this case, load the die information based on id and situation (pool, sid)
			 * Once the information is loaded, roll it by calling the dieroll service and roll.
			 */

			Dice die;
			if (die_id != 0) {
				die = diceRepository.findOneById(die_id);
			} else {
				if (pool == 100) {
					game.put(RESULT_COLUMN, 1);
					die = avatarDiceRepository.findByAvatarAndPriority(batterNpc.getAvatar(), 1).getDice();
				} else if (pool == 101) {
					game.put(RESULT_COLUMN, 2);
					die = avatarDiceRepository.findByAvatarAndPriority(batterNpc.getAvatar(), 2).getDice();
				} else {
					die = diceRepository.findOneByClientAndPoolAndSidSafe((Integer) session.getAttribute(CLIENT_ID), pool, sid);
					if (die == null)
						die = diceRepository.findOneByClientAndPoolAndSidSafe((Integer) session.getAttribute(CLIENT_ID), pool, 0);
				}
			}
			if (die == null) {
				/*
				 *  Something went wrong. Missing resource.
				 */
				throw new NotFoundException("The dice pool " + die_id + COMMA_SEPARATE + pool + COMMA_SEPARATE + sid + "does not exist");
			}

			/*
			 *  Three possible cases:
			 * 			A roll result was provided
			 * 			There is dice information (type, count, rules that apply: APBA uses string)
			 * 			There is position information; which goes further back the higher the number. 1 indicates the end point.
			 */
			boolean threedDice = PHPHelper.isTrue(gameRepository.getStat(gameready.getGame().getId(), RULE_ID_THREED_DICE));
            String DiceResult;
			boolean isDiceRollEntered = false;
			if (roll != null) {
				roll = roll.replaceAll("[*a-zA-Z]", EMPTY_STRING);
			}
			if ((ruleManualRollDice || threedDice) && roll != null && !EMPTY_STRING.equals(roll)) {
				isDiceRollEntered = true;
				List<String> results = new ArrayList<>(Arrays.asList(PHPHelper.explode(VERTICAL_BAR_CHAR, roll)));
				DiceResult = PHPHelper.array_shift(results);
				roll = (results.size() != 0) ? PHPHelper.implode(VERTICAL_BAR, results) : null;
			} else if (PHPHelper.isTrue(die.getRule()) && PHPHelper.is_numeric(die.getRule())) {
				DiceResult = (String) dierolls.get(dierolls.size() - PHPHelper.toInt(die.getRule())).get(ROLL);
			} else {
				DiceResult = dieRollService.roll(die.getDice(), die.getCount(), die.getRule());
			}

			/*
			 *  Get the outcome of the roll; what the numbers mean in this particular situation
			 *
			 *  This may populate multiple results, so iterate through them and test the requirement.
			 *  Requirements will be based on the dieroll.
			 */
			List<DiceResult> dieResults;
			String diceResultNoAsterisk = DiceResult;
			if (DiceResult.endsWith(ASTERISK)) {
				diceResultNoAsterisk = DiceResult.substring(0, DiceResult.length() - 1);
			}
			if (pool == 100 || pool == 101) {
				dieResults = diceResultRepository.findAllByDiceAndDicePoolAndDiceSid(die.getId(), pool, sid);
			} else if ((pool < 10 || pool > 17) && pool != 35) {
				dieResults = diceResultRepository.findAllByDicePoolAndDiceSid(pool, sid);
			} else if (sid_mod > 0) {
				/* Dice result is a hit and run or sac */
				dieResults = diceResultRepository.findAllByDicePoolAndDiceSid(pool, sid_mod);
			} else if (PHPHelper.toInt(diceResultNoAsterisk) < 12) {
				/* Dice result is based on Pitcher Grade ... */
				if (pitcher.isReliefPitcher()) {
					sid = PHPHelper.toInt(avatarRepository.getRes(pitcher.getAvatar(), RELIEF_PITCHER_GRADE_LABEL_LONG));
				} else {
					sid = PHPHelper.toInt(avatarRepository.getRes(pitcher.getAvatar(), PITCHER_GRADE_LABEL));
				}

				if (sid < 4) sid = 4;

				if (pool == 35) sid = (int) game.get(BASE_STATUS_LABEL);
				dieResults = diceResultRepository.findAllByDicePoolAndDiceSid(pool, sid);
				if (dieResults.size() == 0) {
					gameConsoleUtil.writeToConsole(gameready, "No Results: " + pool + EMPTY_SPACE + sid, MESSAGE_PBP);
				}
			} else {
				/* Dice Result is based on Fielding Rating */
				GameNpc team = (GameNpc) game.get(TEAM_LABEL);
				sid = PHPHelper.toInt(gameNpcRepository.getStat(team, RATING_LABEL));
				gameConsoleUtil.writeToConsole(gameready, "Using Fielding " + sid, MESSAGE_PBP);

				if (pool == 35) sid = (int) game.get(BASE_STATUS_LABEL);

				dieResults = diceResultRepository.findAllByDicePoolAndDiceSid(pool, sid);
			}

			Integer forward_pool = null;
			Integer forward_sid = null;
			char[] diceResult = null;
			for (DiceResult dieResult : dieResults) {
				/*
				 *  Test the current result
				 */
				if (dieRollService.result(DiceResult, dieResult.getRequirement())) {
					/*
					 * We have a success result. Now we do some stuff with it.
					 * Including store it.
					 */
					Map<String, Object> event = null;
					switch (dieResult.getTriggerType()) {
						/*
						 *  Cases defined:
						 *  dice 					--> basice result. Store the result, the roll, and go forward based on the result trigger information
						 *  parser 					--> it gets added to the parser array
						 *  dice_generate 			--> Use the result as if it were the roll. Go forward based on trigger information, with defaults
						 *  dice_generate_advance 	--> As dice_generate, changing the default pool.
						 *  dice_result_rating 		--> Go sideways; using the result as the pool and game information based on the result as the sid
						 *  dice_result				--> Use the pool based on the previous result, and sid based on the trigger sid
						 */
						case DICE_LABEL:
							/*
							 *  Roll dice, with pool/sid as defined by the result of the previous roll (trigger values)
							 */
							dierolls.add(diceEvent(dieResult, die, DiceResult));
							forward_sid = dieResult.getTriggerSid();
							forward_pool = dieResult.getTriggerPool();
							break;
						case DICE_GENERATE:
							/*
							 * Roll the dice, storing the result.
							 */
							event = diceEvent(dieResult, die);
							diceResult = PHPHelper.str_split(dieResult.getRequirement());
							
							event.put(MESSAGE_LABEL, getDiceRollMessage(gameready, diceResult, dieResult, batterNpc));

							dierolls.add(event);

							gameConsoleUtil.writeToConsole(gameready, (String) event.get(MESSAGE_LABEL));

							if (sid_mod != 0 && (PHPHelper.toInt(dieResult.getTriggerSid()) == 0))
								forward_sid = sid_mod;
							else
								forward_sid = PHPHelper.toInt(dieResult.getTriggerSid()) != 0 ? dieResult.getTriggerSid() : 1;
							forward_pool = PHPHelper.toInt(dieResult.getTriggerPool()) != 0 ? dieResult.getTriggerPool() : (Integer) game.get(POOL);
							break;
						case DICE_GENERATE_MODIFIER1:
							/*
							 * Roll the dice, then send it through the service call modifier1
							 * After this service call; store the result
							 *
							 */
							
							dieResult = diceModifierService.modifier1(game, dieResult);
							
							// TODO this is probably wrong since we wipe it immediately
							if (event == null) {
								event = new LinkedHashMap<>();
							}
							// Conversion Note: guard against missing
							if (diceResult == null) {
								diceResult = new char[]{EMPTY_SPACE_CHAR, EMPTY_SPACE_CHAR};
							}
							event.put(MESSAGE_LABEL, getDiceRollMessage(gameready, diceResult, dieResult, batterNpc));
							event = diceEvent(dieResult, die);
							dierolls.add(event);
							if (sid_mod != 0)
								forward_sid = sid_mod;
							else
								forward_sid = PHPHelper.toInt(dieResult.getTriggerSid()) != 0 ? dieResult.getTriggerSid() : 1;
							forward_pool = PHPHelper.toInt(dieResult.getTriggerPool()) != 0 ? dieResult.getTriggerPool() : (Integer) game.get(POOL);

							break;
						case DICE_GENERATE_ADVANCE:
							/*
							 * Roll the dice, storing the result.
							 * 		This also changes the default dice pool you return to.
							 */
							event = diceEvent(dieResult, die);
							event.put(MESSAGE_LABEL, "Rolling on RP board ... " + dieResult.getRequirement() + " (" + dieResult.getResultText() + ")");
							dierolls.add(event);

							gameConsoleUtil.writeToConsole(gameready, (String) event.get(MESSAGE_LABEL));

							forward_sid = PHPHelper.toInt(dieResult.getTriggerSid()) != 0 ? dieResult.getTriggerSid() : 1;
							forward_pool = PHPHelper.toInt(dieResult.getTriggerPool()) != 0 ? dieResult.getTriggerPool() : (Integer) game.get(POOL);
							game.put(POOL, PHPHelper.toInt(dieResult.getTriggerPool()) != 0 ? dieResult.getTriggerPool() : (Integer) game.get(POOL));
							break;
						case DICE_RESULT_RATING:
							/*
							 *  Roll the dice, with pool based on the result of the previous roll, and rating derived from the result
							 */
							event = new LinkedHashMap<>();
							event.put(RESULT_LABEL, dieResult);
							event.put(ROLL, DiceResult);
							event.put(DICE_NAME_LABEL, die.getName());
							event.put(MESSAGE_LABEL, "Rolling on Fielding Column Finder Chart ... " + DiceResult + " (" + dieResult.getResultConsole() + ")");
							dierolls.add(event);

							gameConsoleUtil.writeToConsole(gameready, (String) event.get(MESSAGE_LABEL), MESSAGE_PBP);


							forward_pool = dieResult.getTriggerPool();
							forward_sid = PHPHelper.toInt(gameService.getValue(game, dieResult.getResultText(), RATING_LABEL));
							break;
						case DICE_RESULT:
							/*
							 * Roll the dice, with the pool based on the result of the previous roll, and sid based on the trigger_sid
							 */
							event = diceEvent(dieResult, die, DiceResult);
							event.put(MESSAGE_LABEL, "Lookup: " + dieResult.getResultConsole());
							dierolls.add(event);

							gameConsoleUtil.writeToConsole(gameready, (String) event.get(MESSAGE_LABEL));

							forward_pool = (Integer) game.get(POOL);
							forward_sid = dieResult.getTriggerSid();

							break;
						case PARSER:
							/*
							 *  Add information to the parser array.
							 *
							 *  Store the rest of the information, just in case it isn"t an end point.
							 */
							parser.add(dieResult.getResultParser());
							event = diceEvent(dieResult, die, DiceResult);
							dierolls.add(event);
							forward_sid = dieResult.getTriggerSid();
							forward_pool = dieResult.getTriggerPool();
							Integer finalDicePool = dieResult.getDicePool();
							Integer finalDiceSid = dieResult.getDiceSid();
							game.put(PARSE_CONDITION_LABEL, new LinkedHashMap<String, Object>() {{
								put(PAGE, finalDicePool);
								put(SECTION, finalDiceSid);
								put(RESULT_ID_LABEL, DiceResult);
							}});
							break;
					}
					if (isDiceRollEntered && !threedDice) {
						if (event == null) {
							event = new LinkedHashMap<>();
						}
						event.put(MESSAGE_LABEL, MANUAL_DICE_ROLL);
						gameConsoleUtil.writeToConsole(gameready, (String) event.get(MESSAGE_LABEL));
					}
					/*
					 *  If triggered.redirect to parser
					 */
					if (dieResult.getTriggerEnd()) {
						/*
						 * Do some preparatory work.
						 * 	Take the roll information and store it
						 * 	Store the console information
						 *  Store the parser information
						 */
						// Conversion Note: never used
						// parse_out = EMPTY_STRING;

						String dieText = dieResult.getResultConsole();

						if (injuryDice) {
							if (dieText.contains(INJURY_LABEL_2) || dieText.contains(BATTER_INJURED)
									|| dieText.contains(CATCHER_INJURED)) {
								dieText = EMPTY_SPACE + INJURY_DISABLED_MESSAGE;
								rerollInjury = true;
							} else {
								dieText = getDiceText(dieText);
							}
						}

						if (dieText.contains(RAIN) && rainReroll) {
 							dieText = EMPTY_SPACE + RAIN_DISABLED_MESSAGE;
 						}

						String console = "Result: " + dieText;

						gameConsoleUtil.writeToConsole(gameready, console, MESSAGE_PBP);
					} else {
						/*
						 *  Forward unto .. well.. not dawn, but the next roll.
						 * 		Send everything into the next event loop.
						 */

						return doRollAction(session, game, PHPHelper.toInt(dieResult.getTriggerId()), forward_pool, forward_sid, dierolls, parser, sid_mod, roll);
					}
				}
			}
		}
		doRollActionTimer4.endTimer();
		TimerUtil doRollActionTimer5 = new TimerUtil("doRollActionTimer5");
		/*
		 * Ok. We"re done. There was no forwarding, so we call the parser
		 */

		boolean reroll = false;
		String actions = EMPTY_STRING;
		List<String> move_output = new ArrayList<String>();
		
		if (!rerollInjury) {
			List<String> parser_array;
			if (parser.size() > 0) {
				TimerUtil Dice503 = new TimerUtil("Dice503");
				parser_array = apbaParserService.parseAction(game, parser, (boolean) session.getAttribute(OPTIONAL_PITCHING_RULE), (boolean) injuryDice, (boolean) rainReroll, Boolean.TRUE.equals(session.getAttribute(AI)));
				entityManagerUtil.flush(session);
				Dice503.endTimer();
			} else {
				parser_array = new ArrayList<>();
			}
	
			/*
			 * We have the parser results, so display everything.
			 */
	
			move_output = gameService.executeMoves(session, game);
	
			List<String> output_array = new ArrayList<>();
			for (String output : parser_array) {
				if (injuryDice) {
					output = getOutput(output);
				}
				output_array.add(output);
			}
			
			actions = StringUtils.capitalize(PHPHelper.implode(COMMA_SEPARATE, output_array));
		} 
		doRollActionTimer5.endTimer();
		TimerUtil doRollActionTimer6 = new TimerUtil("doRollActionTimer6");
		if (injuryDice && rerollInjury) {
			actions = INJURY_DISABLED_MESSAGE;
			reroll = true;
		}

		if (rainReroll && gameready.getForfeit() == GameService.GAME_RESULT_RAIN) {
			actions = RAIN_DISABLED_MESSAGE;
			gameready.setForfeit(GameService.GAME_RESULT_NORMAL);
			reroll = true;
		}

		gameConsoleUtil.writeToConsole(gameready, actions);
		doRollActionTimer6.endTimer();
		TimerUtil doRollActionTimer7 = new TimerUtil("doRollActionTimer7");
		if (reroll) {
			doRollAction(session, game, INT_ZERO, INT_ONE_HUNDRED, nullableSid, dierolls, new_parser, sid_mod, roll);
		}
		doRollActionTimer7.endTimer();
		TimerUtil doRollActionTimer8 = new TimerUtil("doRollActionTimer8");
		for (String move_line : move_output) {
			gameConsoleUtil.writeToConsole(gameready, move_line);
		}

		if (!gameready.getGameActive()) {
		    endGameService.finishGameJSON(gameready);
        } else {
			Avatar pitcherAvatar = pitcher.getAvatar();
			avatarRepository.makeResExpire(pitcherAvatar.getId());
			int ipInt = PHPHelper.toInt(avatarRepository.getRes(pitcherAvatar, STAT_REF_KEY_IP));
			int ipp = ipInt % 3;
			double ipDouble = ipInt / 3d;
			BigDecimal ip = new BigDecimal((int) Math.floor(ipDouble) + PERIOD + ipp);
	
			AvatarRes grade_modifier_res = avatarRepository.loadRes(pitcher.getAvatar(), GRADE_MODIFIER);
			if (grade_modifier_res == null) {
				grade_modifier_res = new AvatarRes();
				grade_modifier_res.setClient(ClientUtil.client);
				grade_modifier_res.setAvatar(pitcher.getAvatar());
				grade_modifier_res.setRes(ResRepositoryUtil.getRes(GRADE_MODIFIER));
				grade_modifier_res.setStart(-1);
			}
			int grade_modifier = grade_modifier_res.getStart() == null ? INT_ZERO : grade_modifier_res.getStart();
			AvatarRes grade_res = avatarRepository.loadRes(pitcher.getAvatar(), PITCHER_GRADE_LABEL);
			int grade = grade_res.getStart() == null ? INT_ZERO : grade_res.getStart();
			int pitcher_erbInt = PHPHelper.toInt(avatarRepository.getCachedRes(pitcherAvatar, PITCHER_ERB_LABEL));
	
			double pitcher_erbTemp = pitcher_erbInt / 3d;
			BigDecimal pitcher_erb = PHPHelper.roundToBigDecimal(pitcher_erbTemp, 1);
	
			StringBuilder message = new StringBuilder("Pitcher has ");
			message.append(ip).append(" inning");
			if (ip.compareTo(new BigDecimal(1)) != 0) {
				message.append("s");
			}
			message.append(" pitched and last earned run ").append(pitcher_erb);
	
			gameConsoleUtil.writeToConsole((GameReady) game.get(GAME), message.toString(), MESSAGE_TRACE);
			String gradeLetter = EMPTY_STRING;
			
			boolean ruleOptionalPitching = (boolean) session.getAttribute(OPTIONAL_PITCHING_RULE);
			if (ruleOptionalPitching) {
				if (grade == PITCHER_GRADE_D_INT && isNoEarnedRunsRecentInnings(ip.subtract(pitcher_erb), 5)) {
					gradeLetter = PITCHER_GRADE_C;
				}
				if (grade == PITCHER_GRADE_C_INT && isNoEarnedRunsRecentInnings(ip.subtract(pitcher_erb), 6)) {
					gradeLetter = PITCHER_GRADE_B;
				}
				if (grade == PITCHER_GRADE_B_INT && isNoEarnedRunsRecentInnings(ip.subtract(pitcher_erb), 7)) {
					gradeLetter = PITCHER_GRADE_A;
				}
				if (grade == PITCHER_GRADE_A_INT && isNoEarnedRunsRecentInnings(ip.subtract(pitcher_erb), 8)) {
					grade_modifier += 99999;
					grade = PITCHER_GRADE_A_INT;
				}
				
				if (!EMPTY_STRING.equals(gradeLetter)) {
					grade_modifier += 1;
					grade += 1;
					
					message = new StringBuilder();
					message.append(pitcher.getFullName())
						.append(ADVANCED_GRADE)
						.append(gradeLetter)
						.append(EXCLAMATION_POINT);
					
					gameConsoleUtil.writeToConsole(gameready, message.toString());
				}
				
				grade_res.setStart(grade);
				grade_modifier_res.setStart(grade_modifier);
				entityManager.persist(grade_res);
				entityManager.persist(grade_modifier_res);
			}
        }
		doRollActionTimer8.endTimer();
		TimerUtil doRollActionTimer9 = new TimerUtil("doRollActionTimer9");
		gameready = (GameReady) game.get(GAME);
		gameready.setBusy(0);
		entityManager.persist(gameready);
		
        Map<String, Object> response =  new LinkedHashMap<>();
        response.put(GAME, PLAYING_LABEL);
        if(!isAutoplayGame(gameready) || !gameready.getGameActive()) {
        	game = gameService.setUp(session);
    		game.put(ORIGINAL_POOL, pool);
            response = gameService.response(game, session);
        }
		gameready = entityManager.merge(gameready);
		gameready.setBusy(0);
		entityManager.persist(gameready);
		doRollActionTimer9.endTimer();
		TimerUtil doRollActionTimer10 = new TimerUtil("doRollActionTimer10");
		if (!APBAHelper.isSolitaireGame(session)) {
			gameStatusService.markStatusChanged(session, false);
		}
		
		entityManagerUtil.flush(session);

		doRollActionTimer10.endTimer();
		doRollActionTimer.endTimer();
		return response;
	}
	
	private String getDiceRollMessage(GameReady gameReady, char[] diceResult, DiceResult dieResult, GameNpc batterNpc) {
		String tagOpen, tagClosed, logTagOpen, logTagClosed, t, ta;
		
		if (gameReady.getInningTop()) {
			tagOpen = CTA_TAG_OPEN;
			tagClosed = CTA_TAG_CLOSED;
			logTagOpen = LOG_CTA_TAG_OPEN;
			logTagClosed = LOG_CTA_TAG_CLOSED;
			
			t = gameReady.getTeam(Side.AWAY);
			ta = gameReady.getAbbr(Side.AWAY);
		} else {
			tagOpen = CTH_TAG_OPEN;
			tagClosed = CTH_TAG_CLOSED;
			logTagOpen = LOG_CTH_TAG_OPEN;
			logTagClosed = LOG_CTH_TAG_CLOSED;
			
			t = gameReady.getTeam(Side.HOME);
			ta = gameReady.getAbbr(Side.HOME);
		}
		
		return new StringBuilder()
		.append(tagOpen).append(ta).append(tagClosed)
		.append(logTagOpen).append(t).append(logTagClosed)
		.append(" rolls a <d1>").append(diceResult[0])
		.append("</d1> and <d2>").append(diceResult[1])
		.append("</d2> <br /> It's a").append(getDigitVowel(dieResult))
		.append(" ").append(dieResult.getResultText())
		.append(" on <span class='console-player-name'>")
		.append(batterNpc.getFullName())
		.append("'s</span> card.").toString();
	}
	
	private boolean isNoEarnedRunsRecentInnings(BigDecimal inningsWithoutEarnedRuns, int inningDifference) {
		return (inningsWithoutEarnedRuns.compareTo(new BigDecimal(inningDifference)) >= INT_ZERO);
	}
	
	private LinkedHashMap<String, Object> diceEvent(DiceResult dieResult, Dice die) {
		return diceEvent(dieResult, die, dieResult.getResultText());
	}
	
	private LinkedHashMap<String, Object> diceEvent(DiceResult dieResult, Dice die, String DiceResult) {
		return new LinkedHashMap<String, Object>() {{
			put(RESULT_LABEL, dieResult);
			put(ROLL, DiceResult);
			put(DICE_NAME_LABEL, die.getName());
		}};
	}

	/**
	 * Given a result from a dice roll, check to see if the number begins with a vowel,
	 * and if so, append an "n" for cases of form "It's a(n) #"
	 *
	 * @param dieResult
	 * @return blank string, or "n"
	 */
	private String getDigitVowel(DiceResult dieResult) {
		String number = dieResult.getResultText();
		String result = EMPTY_STRING;
		if(number.equals(INT_STR_EIGHT) || 
				number.equals(INT_STR_ELEVEN) || 
				number.equals(INT_STR_EIGHTEEN)) {
			result = "n";
		}
		return result;
	}
	
	private String getDiceText(String diceText) {
		String[] diceArray = diceText.split(SEMICOLON);
		diceText = EMPTY_STRING;
		for (int i = 0; i < diceArray.length; i ++) {
			String text = diceArray[i];
			if (text.contains("but is injured")) {
				text = text.substring(0, text.lastIndexOf(COMMA));
			}
			if (!text.toLowerCase().contains(INJURY_LABEL) && !text.contains("unable to play") &&
					!text.contains("out for remainder")) {
				diceText = diceText.concat(text);
				if (i < (diceArray.length - 1)) {
					diceText = diceText.concat(SEMICOLON);
				}
			}
		}
		return diceText;
	}
	
	private String getOutput(String message) {
		if (message.contains(PINCH_LABEL) && message.contains(LEAVING_LABEL)) {
			message = message.substring(0, message.lastIndexOf(PERIOD));
			String output = message.substring(message.lastIndexOf(PERIOD), message.length()).concat(PERIOD);
			message = message.substring(0, message.lastIndexOf(PERIOD));
			message = message.substring(0, message.lastIndexOf(PERIOD)).concat(output);
			
		} else if (message.contains(INJURY_LABEL_3) && message.contains(LEAVE_LABEL)) {
			while (message.contains(INJURY_LABEL_3)) {
				message = message.substring(0, message.lastIndexOf(PERIOD));
			}
		} else {
			while (message.contains(INJURY_LABEL_2)) {
				message = message.substring(0, message.lastIndexOf(PERIOD));
			}
			while (message.contains(INJURY_LABEL)) {
				if (message.contains(";")) {
					message = message.substring(0, message.lastIndexOf(SEMICOLON));
				} else if (message.contains(".")) {
					message = message.substring(0, message.lastIndexOf(PERIOD));
				} else {
					message = message.substring(0, message.lastIndexOf(COMMA));
				}
			}
			while (message.contains("unable to continue in the game")) {
				message = message.substring(0, message.lastIndexOf(PERIOD));
			}
		}
		return message;
	}
}
