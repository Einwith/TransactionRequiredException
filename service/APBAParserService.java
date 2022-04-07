package com.lixar.apba.service;

import static com.lixar.apba.core.util.GameConstants.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.lixar.apba.web.rest.util.GameConsoleUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lixar.apba.core.util.JSONUtil;
import com.lixar.apba.core.util.PHPHelper;
import com.lixar.apba.core.util.StringUtil;
import com.lixar.apba.domain.Avatar;
import com.lixar.apba.domain.AvatarDice;
import com.lixar.apba.domain.AvatarRes;
import com.lixar.apba.domain.AvatarStat;
import com.lixar.apba.domain.BookEntry;
import com.lixar.apba.domain.Client;
import com.lixar.apba.domain.DiceResult;
import com.lixar.apba.domain.GameNpc;
import com.lixar.apba.domain.GameReady;
import com.lixar.apba.repository.AvatarDiceRepository;
import com.lixar.apba.repository.AvatarRepository;
import com.lixar.apba.repository.BookEntryRepository;
import com.lixar.apba.repository.DiceResultRepository;
import com.lixar.apba.repository.GameNpcRepository;
import com.lixar.apba.repository.util.ResRepositoryUtil;
import com.lixar.apba.service.util.ClientUtil;
import com.lixar.apba.service.util.EntityManagerUtil;
import com.lixar.apba.service.util.TimerUtil;
import com.lixar.apba.web.NotFoundException;

import static com.lixar.apba.core.util.GameConstants.RESULT_COLUMN;

//Converted from APBAParserController. It extends ParserController, but overrides all functions
@Service
public class APBAParserService {


	// Constants for tracking in the earned_runners stat
	public static final String RUNNERS_ACTIVE = "active";
	public static final String RUNNERS_ID = "id";
	public static final String RUNNERS_NAME = "name";
	public static final String RUNNERS_PITCHER_ID = "pitcher";
	public static final String RUNNERS_EARNED_MOVE = "earned_move";
	public static final String RUNNERS_UNEARNED_MOVE = "unearned_move";
	public static final String RUNNERS_BATTER_EARNED_MOVE = "batter_earned_move";
	public static final String RUNNERS_ALWAYS_UNEARNED = "always_unearned";
	public static final String RUNNERS_EARNED_RUN = "earned_run";
	public static final String RUNNERS_NEW = "new";
	public static final String RUNNERS_MODIFIER = "modifier";

	public static final String ACTION_MOVE = "move";
	public static final String ACTION_UNEARNED_PRIORITY = "unearned_priority";
	public static final String ACTION_PRIORITY_MOVE = "priority_move";

	public static final String POSITION_BATTER = "bt";
	public static final String POSITION_RUNNER1 = "r1";
	public static final String POSITION_RUNNER2 = "r2";
	public static final String POSITION_RUNNER3 = "r3";

	public static final String EARNED_RUN_REASSESS = "reassess";
	public static final String STRIKEOUT_MESSAGE = " Batter strikes out.";

	public static final boolean LOG_EXCESSIVE_EARNED_MOVES = false;

	public static final int MAXIMUM_BALLS = 4;
	public static final int MAXIMUM_STRIKES = 3;

	private final Logger log = LoggerFactory.getLogger(APBAParserService.class);

	@Inject
	private GameNpcRepository gameNpcRepository;

	@Inject
	private AvatarRepository avatarRepository;

	@Inject
	private AvatarDiceRepository avatarDiceRepository;

	@Inject
	private DiceResultRepository diceResultRepository;

	@Inject
	private BookEntryRepository bookEntryRepository;
	
	@Inject
	private EndGameService endGameService;

	@Inject
	private GameConsoleUtil gameConsoleUtil;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
	private EntityManagerUtil entityManagerUtil;

	private boolean hasProperConditions(Map<String, Object> book_vals) {
		if (book_vals == null) {
			return false;
		}

		if ((book_vals.get("page") == null) ||
				(book_vals.get("section") == null) ||
				(book_vals.get("result_id") == null) ||
				(book_vals.get("result_book") == null)) {
			return false;
		}

		return true;
	}

	public List<String> act(Map<String, Object> game, Movements movements, String action, boolean isAI) {
		
		//Conversion note: session was not used
		int advance_chance = 40;
		int steal_chance = 41;
		int dropped_throwing_pool = 30;
		int dropped_stealing_pool = 31;
		int wild_second_pool = 32;
		int wild_third_pool = 33;
		int wild_catcher_pool = 34;
		Map<String, Object> book_vals = (Map<String, Object>) game.get("parse_condition");

		List<BookEntry> book_entry = hasProperConditions(book_vals) ?
				bookEntryRepository.findAllByPageAndSectionAndResultIdAndResultBook(
						(int) book_vals.get("page"), (int) book_vals.get("section"), (String) book_vals.get("result_id"),
						(String) book_vals.get("result_book"))
				: null;

		String[] actions = action.split(",");
		List<String> output = new ArrayList<>();
		boolean wp = false;
		int balk = 0;
		int base_on_balls = 0;
		int hbp = 0;

		List<String> remove_list = new ArrayList<>();
		boolean next_action = false;
		//Conversion note: it will be used as string later
		String newText = null;
		GameReady gameready = (GameReady) game.get("game");
		GameNpc npc = (GameNpc) game.get("catcher");


		for (String act : actions) {
			String[] act_array = act.split("\\-");

			switch (act_array[0]) {
				case "steal":
					if ((act_array.length > 2) && "caught".equals(act_array[2])) {
						output.add(((GameNpc) game.get(GameService.translate(act_array[1]))).getFullName() + " has been caught stealing a base");
						updateStat(game, act_array[1], "cs");
						updateStat(game, "c", "ocs");
						trackErrorOrOut(movements, act_array[1]);
						Map<String, Integer> moves = (Map<String, Integer>) game.get("moves");
						moves.put(act_array[1], -1);
						game.put("moves", moves);
						game.put("new_batter", false);
					} else {
						String nextBase = getNextBase(act_array[1]);
						output.add(((GameNpc) game.get(GameService.translate(act_array[1]))).getFullName() + " has stolen " + nextBase);
						moveRunner(game, act_array[1], 1);
						trackEarnedMove(game, movements, act_array[1], 1, ACTION_MOVE);
						updateStat(game, act_array[1], "steal");
						updateStat(game, "c", "osb");
					}
					break;
				case "pickedoff":
					output.add(((GameNpc) game.get(GameService.translate(act_array[1]))).getFullName() + " has been picked off!");
					game.put("new_batter", false);
					break;
				case "so":
					//Strikeout
					npc = (GameNpc) game.get("batter");
					output.add("Batter strikes out");
					updateStat(game, "p", "pitcher_strikeout");
					updateStat(game, "bt", "strikeout");
					break;
				case "wp":
					//Wild Pitch
					npc = (GameNpc) game.get("pitcher");
					output.add(npc.getFullName() + " throws a wild pitch");
					updateStat(game, "p", "wp");
					wp = true;
					break;
				case "pb":
					//Passed Ball
					npc = (GameNpc) game.get("catcher");
					output.add("Passed ball by " + npc.getFullName());
					updateStat(game, "c", "pb");
					break;
				case "dp":
					//Player has been part of a double play
					npc = (GameNpc) game.get(GameService.translate(act_array[1]));
					output.add(npc.getFullName() + " helps turn a double play");
					GameReady gr = (GameReady) game.get("game");
					if (gr.getCso() < 2) {
						updateStat(game, act_array[1], "doubleplay");
					}
					break;
				case "tp":
					//Player has been part of a triple play
					npc = (GameNpc) game.get(GameService.translate(act_array[1]));
					//TODO probably should be " helps turn a TRIPLE play"
					output.add(npc.getFullName() + " helps turn a double play");
					gr = (GameReady) game.get("game");
					updateStat(game, act_array[1], "tripleplay");
					break;
				case "rain":
					//Game over. Rain
					output.add("Game called due to rain");
					gameready.setForfeit(GameService.GAME_RESULT_RAIN);
					if (!movements.optionalRain) {
						endGameService.finishGame(gameready, isAI);
						entityManager.persist(gameready);
					}
					break;
				case "priority_move":
					//Move the batter/runner x bases, without adding stats for hits; occurs before the inning ends in the case of 2 outs
					addPlayerAdvancementToOutput(game, act_array, output);
					moveRunner(game, act_array[1], 100 + PHPHelper.toInt(act_array[2]));
					trackEarnedMove(game, movements, act_array[1], PHPHelper.toInt(act_array[2]), act_array[0]);
					break;
				case "unearned_priority":
					addPlayerAdvancementToOutput(game, act_array, output);
					moveRunner(game, act_array[1], 100 + PHPHelper.toInt(act_array[2]));
					trackUnearnedMove(game, movements, act_array[1], PHPHelper.toInt(act_array[2]), act_array[0]);
					break;
				case "move":
					//Move the batter/runner x bases, without adding stats for hits
					addPlayerAdvancementToOutput(game, act_array, output);

					if ("bt".equals(act_array[1])) {
						game.put("atbat", true);
					}
					
					moveRunner(game, act_array[1], PHPHelper.toInt(act_array[2]));
					trackEarnedMove(game, movements, act_array[1], PHPHelper.toInt(act_array[2]), act_array[0]);
					break;
				case "unearned_move":
					addPlayerAdvancementToOutput(game, act_array, output);

					if ("bt".equals(act_array[1])) {
						game.put("atbat", true);
					}
					
					moveRunner(game, act_array[1], PHPHelper.toInt(act_array[2]));
					trackUnearnedMove(game, movements, act_array[1], PHPHelper.toInt(act_array[2]), act_array[0]);
					break;
				case "hbp":
					//Pitcher hit batter
					npc = (GameNpc) game.get("pitcher");
					GameNpc npc2 = (GameNpc) game.get("batter");
					output.add("No brushback here, " + npc2.getFullName() + " is hit by pitch");
					updateStat(game, "bt", "hbp");
					updateStat(game, "p", "hitbatter");
					game.put("noatbat", true);
					hbp = 1;
					break;
				case "bb":
					//Track that this is a base-on-balls
					//-i , -n
					//Conversion note: act_array[1] probably can't be null, only not exist
					if ((act_array.length < 2) || (act_array[1] == null)) {
						String tmp = act_array[0];
						act_array = new String[2];
						act_array[0] = tmp;
						act_array[1] = "n";
					}

					if ("i".equals(act_array[1])) {
						output.add(((GameNpc) game.get("pitcher")).getFullName() + " issues an intentional base on balls");
						updateStat(game, "p", "pitcher_ibb");
						updateStat(game, "bt", "ibb");
					} else {
						output.add(((GameNpc) game.get("batter")).getFullName() + " draws a walk");
						updateStat(game, "p", "pitcher_bb");
						updateStat(game, "bt", "bb");
					}
					base_on_balls = 1;
					movements.batterMovedOnBalls = true;
					game.put("noatbat", true);
					break;
				case "sac": // Sacrifice hit
					updateStat(game, "bt", "sh");
					updateStat(game, "p", "pitcher_sh");
					game.put("noatbat", true);
					output.add("Play is scored as sacrifice bunt for " + ((GameNpc) game.get("batter")).getFullName());
					break;
				case "sacfly": // Sacrifice fly
					updateStat(game, "bt", "sf");
					updateStat(game, "p", "pitcher_sf");
					game.put("noatbat", true);
					output.add(((GameNpc) game.get("batter")).getFullName() + " sacrifices");
					break;
				case "tag":
					String[] tags = act_array[1].split("\\+");
					for (String tag : tags) {
						String[] tagx = tag.split("\\=");
						tag = tagx[0];
						if ("atbat".equals(tag)) {
							game.put("atbat", true);
						} else if ("noatbat".equals(tag)) {
							game.put("noatbat", true);
						} else if ("rbi".equals(tag)) {
							updateStat(game, "bt", "rbi", PHPHelper.toInt(tagx[1]));
							game.put("force_rbi", true);
						}
					}
					break;
				case "single":
					//Advance one base
					npc = (GameNpc) game.get("batter");
					output.add(npc.getFullName() + " singles");
					updateStat(game, act_array[1], "single");
					updateStat(game, "p", "pitcher_hits");
					game.put("atbat", true);
					moveRunner(game, "bt", 1);
					trackEarnedMove(game, movements, POSITION_BATTER, 1, act_array[0]);
					updateGame(game, "Hits", 1);
					break;
				case "double":
					//Advance two bases
					npc = (GameNpc) game.get("batter");
					output.add(npc.getFullName() + " doubles");
					updateStat(game, act_array[1], "double");
					updateStat(game, "p", "pitcher_hits");
					game.put("atbat", true);
					updateStat(game, "p", "pitcher_2b");
					trackEarnedMove(game, movements, POSITION_BATTER, 2, act_array[0]);
					moveRunner(game, "bt", 2);
					updateGame(game, "Hits", 1);
					break;
				case "triple":
					npc = (GameNpc) game.get("batter");
					output.add(npc.getFullName() + " triples");
					updateStat(game, act_array[1], "triple");
					updateStat(game, "p", "pitcher_hits");
					updateStat(game, "p", "pitcher_3b");
					game.put("atbat", true);
					trackEarnedMove(game, movements, POSITION_BATTER, 3, act_array[0]);
					moveRunner(game, "bt", 3);
					updateGame(game, "Hits", 1);
					//Advance three bases
					break;
				case "homerun":
					npc = (GameNpc) game.get("batter");
					output.add(npc.getFullName() + " goes deep - HOME RUN!");
					updateStat(game, "bt", "homerun");
					updateStat(game, "p", "pitcher_hits");
					game.put("atbat", true);
					updateStat(game, "p", "pitcher_hr");
					trackEarnedMove(game, movements, POSITION_BATTER, 4, act_array[0]);
					moveRunner(game, "bt", 4);
					updateGame(game, "Hits", 1);
					//Four bases; home run
					break;
				case "o":
					//Out, tagged or caught
					npc = (GameNpc) game.get(GameService.translate(act_array[1]));
					output.add(npc.getFullName() + " is out");
					trackErrorOrOut(movements, act_array[1]);
					Map<String, Integer> moves = (Map<String, Integer>) game.get("moves");
					moves.put(act_array[1], -1);
					if ("bt".equals(act_array[1])) {
						game.put("atbat", true);
					}
					game.put("moves", moves);
					break;
				case "ero":
					//Out for the purposes of an Earned Run
					trackErrorOrOut(movements, act_array[1]);
					break;
				case "po":
					//Put out attributed to
					npc = (GameNpc) game.get(GameService.translate(act_array[1]));
					output.add("Put out by " + npc.getFullName());
					updateStat(game, act_array[1], "putout");
					break;
				case "a":
					//Assist given to
					npc = (GameNpc) game.get(GameService.translate(act_array[1]));
					output.add("Assist by " + npc.getFullName());
					updateStat(game, act_array[1], "assist");
					break;
				case "e":
					//Error by
					npc = (GameNpc) game.get(GameService.translate(act_array[1]));
					output.add("Fielding error by " + npc.getFullName());
					updateStat(game, act_array[1], "error");
					gr = (GameReady) game.get("game");
					if (gr.getInningTop()) {
						gr.setHomeErrors(gr.getHomeErrors() + 1);
					} else {
						gr.setAwayErrors(gr.getAwayErrors() + 1);
					}
					entityManager.persist(gr);

					trackErrorOrOut(movements, act_array[1]);

					break;
				case "balls":
					//Add balls to current batter
					//Conversion note: act_array[1] probably can't be null, only not exist
					if ((act_array.length < 2) || (act_array[1] == null)) {
						String tmp = act_array[0];
						act_array = new String[2];
						act_array[0] = tmp;
						act_array[1] = "1";
					}
					npc = (GameNpc) game.get("pitcher");
					if (PHPHelper.toInt(act_array[1]) > 1) {
						output.add(npc.getFullName() + " has thrown " + act_array[1] + " balls");
					} else {
						output.add(npc.getFullName() + " has thrown a ball");
					}
					updateGame(game, "Balls", PHPHelper.toInt(act_array[1]));

					if (((GameReady) game.get("game")).getBalls() >= 4) {
						npc = (GameNpc) game.get("batter");
						output.add(npc.getFullName() + " draws a walk");
						updateStat(game, "p", "pitcher_bb");
						updateStat(game, "bt", "bb");
						base_on_balls = 1;
						trackEarnedMove(game, movements, POSITION_BATTER, 1, ACTION_MOVE);
						moveRunner(game, "bt", 1);
						game.put("noatbat", true);
						movements.batterMovedOnBalls = true;

						if (game.get("runner1") != null) {
							if (game.get("runner2") != null) {
								if (game.get("runner3") != null) {
									output.add(((GameNpc) game.get("runner3")).getFullName() + " advances 1 base");
									moveRunner(game, "r3", 1);
									trackEarnedMove(game, movements, POSITION_RUNNER3, 1, ACTION_MOVE);
								}
								output.add(((GameNpc) game.get("runner2")).getFullName() + " advances 1 base");
								moveRunner(game, "r2", 1);
								trackEarnedMove(game, movements, POSITION_RUNNER2, 1, ACTION_MOVE);
							}
							output.add(((GameNpc) game.get("runner1")).getFullName() + " advances 1 base");
							moveRunner(game, "r1", 1);
							trackEarnedMove(game, movements, POSITION_RUNNER1, 1, ACTION_MOVE);
						}
					}
					break;
				case "fouls":
				case "foul":
					//Add fouls to current batter
					npc = (GameNpc) game.get("batter");
					output.add(npc.getFullName() + " hits a foul ball");
					if (gameready.getCso() < 2) {
						updateGame(game, "Strikes", 1);
					}
					break;
				case "strikes":
					//Add strikes to current batter
					npc = (GameNpc) game.get("batter");

					//Conversion note: act_array[1] probably can't be null, only not exist
					if ((act_array.length < 2) || (act_array[1] == null)) {
						String tmp = act_array[0];
						act_array = new String[2];
						act_array[0] = tmp;
						act_array[1] = "1";
					}
					int balls = getBalls(game, 0);
					int strikes = getStrikes(game, PHPHelper.toInt(act_array[1]));
					output.add("the count is " + balls + " and " + strikes + " against " + npc.getFullName());
					updateGame(game, "Strikes", PHPHelper.toInt(act_array[1]));

					if (((GameReady) game.get("game")).getStrikes() >= 3) {
						output.add(npc.getFullName() + " has struck out.");
						npc = (GameNpc) game.get(GameService.translate("c"));
						output.add("Put out by " + npc.getFullName());
						updateStat(game, "p", "pitcher_strikeout");
						updateStat(game, "bt", "strikeout");
						updateStat(game, "c", "putout");
						trackErrorOrOut(movements, POSITION_BATTER);
						moves = (Map<String, Integer>) game.get("moves");
						moves.put("bt", -1);
						if ("bt".equals(act_array[1])) {
							game.put("atbat", true);
						}
						game.put("moves", moves);
					}
					break;
				case "ejected":
					//Kicked from the game
					npc = (GameNpc) game.get(GameService.translate(act_array[1]));
					output.add(npc.getFullName() + " gets a game ejection");
					updateStat(game, act_array[1], "ejected");
					remove_list.add(act_array[1]);
					break;
				case "remove":
					//Removed from game; not ejected or injured
					npc = (GameNpc) game.get(GameService.translate(act_array[1]));
					output.add(npc.getFullName() + " is done for the night");
					remove_list.add(act_array[1]);
					break;
				case "injury":
					//Injury
					if (!movements.optionalInjury) {
						npc = (GameNpc) game.get(GameService.translate(act_array[1]));
						remove_list.add(act_array[1]);

						Avatar avatar = ((GameNpc) game.get(GameService.translate(act_array[1]))).getAvatar();
						updateStat(game, act_array[1], "injury", PHPHelper.toInt(act_array[2]));
						output.add("INJURED! " + npc.getFullName() + " is down for " + act_array[2] + " games!");
					}
					break;
				case "delay":
					//Game delayed/interrupted for one reason or another
					output.add("Game has been interrupted");
					break;
				case "balk":
					//Balk
					npc = (GameNpc) game.get("pitcher");
					output.add("Umpire calls a balk on " + npc.getFullName());
					updateStat(game, "p", "balk");
					balk = 1;
					break;
				case "buff":
					//We apply an effect to the specified player ..
					next_action = true;
					//Conversion note: array_slice + implode was changed to loop with StringBuilder
					StringBuilder sb = new StringBuilder();
					for (int i = 1; i < act_array.length; i++) {
						sb.append(act_array[i]);
						sb.append("-");
					}
					sb.setLength(sb.length() - 1);
					String comm = sb.toString();
					List<String> comms = new ArrayList<>();
					if (StringUtil.phpStrToBool(gameready.getNextCommand())) {
						comms.add(gameready.getNextCommand());
					}
					comms.add(comm);
					gameready.setNextCommand(StringUtils.join(comms, ","));
					break;
				case "donothing":
					break;
				case "text":
					String[] commArr = new String[act_array.length - 1];
					for (int i = 1; i < act_array.length; i++) {
						commArr[i - 1] = act_array[i];
					}
					newText = StringUtils.join(commArr, "-");
					break;
				default:
					//Conversion note: var $game is not used
					gameConsoleUtil.writeToConsole(gameready, "Unknown Play Result: " + act, MESSAGE_TRACE);
			}
		}

		for (String removed : remove_list) {
			int sub_priority = GameReady.REPLACEMENT_INJURED;
			if ("bt".equals(removed) || "r1".equals(removed) || "r2".equals(removed) || "r3".equals(removed)) {
				gameready.addReplacementMark(!gameready.getInningTop(), sub_priority);
			} else {
				if ("P".equals(removed)) {
					sub_priority = GameReady.REPLACEMENT_INJURED_PITCHER;
				}
				gameready.addReplacementMark(gameready.getInningTop(), sub_priority);
			}
		}

		if (next_action) {
			gameready.setNextCommand("default@" + gameready.getNextCommand());
		}
		game.put("game", gameready);
		entityManager.persist(gameready);

		if ((boolean) game.get("noatbat") == false) {
			if ((boolean) game.get("atbat")) {
				updateStat(game, "bt", "atbat");
			}
		}

		if (((book_entry != null) && (book_entry.size() > 0)) || StringUtil.phpStrToBool(newText)) {
			String string;
			if (StringUtil.phpStrToBool(newText)) {
				string = newText;
			} else {
				String default_string = "";
				String special_string = "";
				for (BookEntry entry : book_entry) {
					if ("default".equals(entry.getTextCondition())) {
						default_string = entry.getResultCurrent();
					} else {
						gameConsoleUtil.writeToConsole(gameready, "Sub-Result: " + entry.getTextCondition(), MESSAGE_PBP);
						String cond = entry.getTextCondition();
						if (checkCond(game, cond)) {
							special_string = entry.getResultCurrent();
						}
					}
				}
				if (StringUtil.phpStrToBool(special_string)) {
					string = special_string;
				} else {
					string = default_string;
				}
			}

			string = string.replace("[1]", ((GameNpc) game.get("pitcher")).getFullName());
			string = string.replace("[2]", ((GameNpc) game.get("catcher")).getFullName());
			string = string.replace("[3]", ((GameNpc) game.get("first")).getFullName());
			string = string.replace("[4]", ((GameNpc) game.get("second")).getFullName());
			string = string.replace("[5]", ((GameNpc) game.get("third")).getFullName());
			string = string.replace("[6]", ((GameNpc) game.get("shortstop")).getFullName());
			string = string.replace("[7]", ((GameNpc) game.get("left")).getFullName());
			string = string.replace("[8]", ((GameNpc) game.get("center")).getFullName());
			string = string.replace("[9]", ((GameNpc) game.get("right")).getFullName());
			string = string.replace("[b]", ((GameNpc) game.get("batter")).getFullName());

			if (game.get("runner1") != null) {
				string = string.replace("[r1]", ((GameNpc) game.get("runner1")).getFullName());
			}
			if (game.get("runner2") != null) {
				string = string.replace("[r2]", ((GameNpc) game.get("runner2")).getFullName());
			}
			if (game.get("runner3") != null) {
				string = string.replace("[r3]", ((GameNpc) game.get("runner3")).getFullName());
			}

			String[] stringArr = string.split("\\|");
			if (stringArr.length == 2) {
				gameready.setNextCommand(gameready.getNextCommand() + ",text-" + stringArr[1]);
				entityManager.persist(gameready);
			}
			string = stringArr[0];

			// Check Strikeout scenario
			Map<String, Integer> moves = (Map<String, Integer>) game.get("moves");
			if ((string.indexOf("Strike two.") > 0) && (moves.get("bt") == -1)) {
				string = string + STRIKEOUT_MESSAGE;
			}
			List<String> result = new ArrayList<String>();
			result.add(string);
			
			return result;
		}

		
		return output;
	}
	
	public void addPlayerAdvancementToOutput(Map<String, Object> game, String[] act_array, List<String> output) {
	    GameNpc npc = (GameNpc) game.get(GameService.translate(act_array[1]));
	    if (PHPHelper.toInt(act_array[2]) > 1) {
	        output.add(npc.getFullName() + " advances " + act_array[2] + " bases");
	    } else {
	        output.add(npc.getFullName() + " advances 1 base");
	    }
	}

	private String getNextBase(String position) {
		switch (position) {
			case "bt":
				return "first";
			case "r1":
				return "second";
			case "r2":
				return "third";
			default:
				return "home";
		}
	}

	private int getBalls(Map<String, Object> game, int increment) {
		int balls = ((GameReady) game.get("game")).getBalls() + increment;
		return balls > MAXIMUM_BALLS ? MAXIMUM_BALLS : balls;
	}

	private int getStrikes(Map<String, Object> game, int increment) {
		int strikes = ((GameReady) game.get("game")).getStrikes() + increment;
		return strikes > MAXIMUM_STRIKES ? MAXIMUM_STRIKES : strikes;
	}

	/**
	 * Convert the parse strings into cond/act pairs
	 *
	 * @param parse
	 * @return array
	 * @throws Exception
	 */
	public List<String> parseAction(Map<String, Object> game, List<String> parse, boolean optionalPitchingRule, boolean optionalInjury, boolean optionalRain, boolean isAI) throws NotFoundException {
		TimerUtil parseActionTimer = new TimerUtil("parseActionTimer");
		List<String> o = null;
		try {
			//may be changed!
			Movements movements = new Movements();
			movements.optionalPitchingRule = optionalPitchingRule;
			movements.optionalInjury = optionalInjury;
			movements.optionalRain = optionalRain;
			String[] parse_array = parse.get(0).split("@");

			if (parse_array.length != 2) {
				throw new NotFoundException("Parse string (" + parse.get(0) + ") is invalid");
			}

			String[] parse_conds = parse_array[0].split(";");
			String[] parse_acts = parse_array[1].split(";");

			if (parse_conds.length != parse_acts.length) {
				//Invalid string
				throw new NotFoundException("Condition and action counts (" + parse.get(0) + ") do not match.");
			}

			Map<String, Object> output = new LinkedHashMap<>();
			output.put("cond_count", 0);
TimerUtil parse728 = new TimerUtil("parse728");

			for (int i = 0; i < parse_conds.length; i++) {
				output.put("cond_count", (int) output.get("cond_count") + 1);
				output.put("cond_" + (i + 1), parse_conds[i]);
				output.put("act_" + (i + 1), parse_acts[i]);
			}
			parse728.endTimer();
			TimerUtil parse736 = new TimerUtil("parse736");
			for (int i = (int) output.get("cond_count"); i > 0; i--) {
				if (checkCond(game, (String) output.get("cond_" + i))) {
					Map<String, Object> x = (Map<String, Object>) game.get("parse_condition");
					if (x == null) {
						x = new LinkedHashMap<>();
					}
					x.put("result_book", output.get("cond_" + i));
					game.put("parse_condition", x);
					TimerUtil actTimer = new TimerUtil("actTimer");
					o = act(game, movements, (String) output.get("act_" + i), isAI);
					actTimer.endTimer();
					break;
				}
			}
			parse736.endTimer();
			TimerUtil processEarnedRunInformationTimerInner = new TimerUtil("processEarnedRunInformationTimerInner");
			processEarnedRunInformation(game, movements);
			processEarnedRunInformationTimerInner.endTimer();
		} catch (NotFoundException e) {
			log.error("Exception occured in parseAction ", e);
			throw e;
		}
		parseActionTimer.endTimer();
		return o;
	}

	private void processEarnedRunInformation(Map<String, Object> game, Movements move) {
		TimerUtil processEarnedRunInformationTimer = new TimerUtil("processEarnedRunInformationTimer");
		if (move.priorityTracking.size() > 0) {
			recordEarnedMove(game, move, move.priorityTracking.get(0), 0, EARNED_RUN_REASSESS);
		}

		for (String position : move.delayedOutTracking) {
			recordErrorOrOut(game, position);
		}

		for (List<Object> info : move.delayedUnearnedMoveTracking) {
			recordUnearnedMove(game, (String) info.get(0), (int) info.get(1));
		}

		sortDelayedMoveTracking(move);

		if (move.delayedMoveTracking.size() > 0) {
			List<Object> newElement = new ArrayList<>();
			newElement.add(move.delayedMoveTracking.get(0).get(0));
			newElement.add(0);
			newElement.add(EARNED_RUN_REASSESS);
			move.delayedMoveTracking.add(newElement);
		}

		for (List<Object> info : move.delayedMoveTracking) {
			recordEarnedMove(game, move, (String) info.get(0), (int) info.get(1), (String) info.get(2));
		}
		processEarnedRunInformationTimer.endTimer();
	}

	private void sortDelayedMoveTracking(Movements move) {
		String[] order = {POSITION_BATTER, POSITION_RUNNER1, POSITION_RUNNER2, POSITION_RUNNER3};
		List<List<Object>> orderedTracking = new ArrayList<>();
		for (String position : order) {
			for (List<Object> info : move.delayedMoveTracking) {
				if (position.equals((String) info.get(0))) {
					orderedTracking.add(info);
				}
			}
		}

		move.delayedMoveTracking = orderedTracking;
	}

	public boolean checkCond(Map<String, Object> game, String cond) {
		TimerUtil checkCondTimer = new TimerUtil("checkCondTimer");
		//we check what condition we are looking for
		List<String> cond_list = null;
		if ("default".equals(cond)) {
			checkCondTimer.endTimer();
			return true;
		} else if ((boolean) game.get("pitching_changes")) {
			checkCondTimer.endTimer();
			return false;
		} else {
			cond_list = new ArrayList<String>();
			if (cond.indexOf(",") > 0) { //check if it we have more than one condition to fill
				cond_list.addAll(Arrays.asList(cond.split(",")));
			} else {
				cond_list.add(cond); //only one condition
			}
		}

		int _false = 0; // set the return true
		for (String cont_part : cond_list) {
			/*
			 *  PHPTODO Special Case: buff that forces a default condition. Goes here. If buff && !default, return false
			 */
			cont_part = cont_part.toLowerCase();
			String[] type = cont_part.split("\\-"); // we get the 3 part of the condition split
			Object d = game.get(type[0]); // we grab the array within the data

			//we check the statistic
			if (type[2].indexOf("|") > 0) { //we check if the condition is something else than a ==
				String[] cond_val = type[2].split("\\|");
				if ("<".equals(cond_val[1])) {
					// e.g. pitcher-grade-6|< means "match pitcher grade < 6", so grade >= 6 should fail
					if (PHPHelper.toInt(getTypeCommand(game, d, type[1], type[0], cond_val[0])) >= PHPHelper.toInt(cond_val[0])) {
						_false++;
					}
				} else if (">".equals(cond_val[1])) {
					// e.g. pitcher-grade-6|> means "match pitcher grade > 6", so grade <= 6 should fail
					if (PHPHelper.toInt(getTypeCommand(game, d, type[1], type[0], cond_val[0])) <= PHPHelper.toInt(cond_val[0])) {
						_false++;
					}
				} else if ("!".equals(cond_val[1])) {
					if (PHPHelper.toInt(getTypeCommand(game, d, type[1], type[0], cond_val[0])) == PHPHelper.toInt(cond_val[0])) {
						_false++;
					}
				}
			} else {
				//we check if the value in the cond is not true
				//Conversion note: type[2] can be number or string
				if (NumberUtils.isDigits(type[2])) {
					if (PHPHelper.toInt(getTypeCommand(game, d, type[1], type[0], type[2])) != PHPHelper.toInt(type[2])) {
						_false++;
					}
				} else {
					if (!type[2].toLowerCase().equals(getTypeCommand(game, d, type[1], type[0], type[2]))) {
						_false++;
					}
				}
			}
		}
		checkCondTimer.endTimer();
		//we check the statistic
		if (_false == 0) {
			return true;
		} else {
			return false;
		}
		
	}


	protected String getTypeCommand(Map<String, Object> game, Object actor, String value, String extra, String expectedValue) {
		String ret = null;
		switch (value) {
			case "column":
				/* XXX double column cards ... */
				List<AvatarDice> resultList = avatarDiceRepository.findAllByAvatar(((GameNpc) actor).getAvatar());
				ret = Integer.toString(resultList.size());
				break;
			case "carddata":
				AvatarDice result = avatarDiceRepository.findByAvatarAndPriority(((GameNpc) actor).getAvatar(), 1);
				List<DiceResult> results = diceResultRepository.findAllByDiceAndResultText(result.getDice().getId(), "11");
				ret = Integer.toString(results.size());
				break;
			case "anycolumndata":
				List<AvatarDice> avatarDiceResults = avatarDiceRepository.findAllByAvatar(((GameNpc) actor).getAvatar());
				if ((avatarDiceResults == null) || (avatarDiceResults.size() == 0)) {
					ret = "";
				} else {
					List<Integer> dice = new ArrayList<>();
					for (AvatarDice resultIter : avatarDiceResults) {
						dice.add(resultIter.getDice().getId());
					}

					String[] requirements = expectedValue.split("\\+");
					results = diceResultRepository.findAllByDiceInAndResultTextIn(dice, requirements);
					ret = results.size() > 0 ? expectedValue : ""; // return the expected so it matches
				}
				break;
			case "infield":
				/* XXX infield is close/deep */
				GameReady gr = (GameReady) game.get("game");
				ret = gr.getGameInfield();
				break;
			case "score_diff":
				ret = Integer.toString(((GameReady) actor).getLead());
				break;
			case "rating":
				gameNpcRepository.getStat((GameNpc) actor, "rating");
				break;
			case "grade":
				ret = avatarRepository.getRes(((GameNpc) actor).getAvatar(), "pg");
				break;
			case "held":
				/* XXX runner held on first */
				gr = (GameReady) game.get("game");
				if ("runner1".equals(extra)) {
					ret = gr.getR1Held();
				} else if ("runner3".equals(extra)) {
					ret = gr.getR3Held();
				} else {
					ret = "";
				}
				break;
			case "htr":
				// e.g. game-htr-1, i.e. Runner 1: Play it Safe
				// Thus, $actor is game(Ready), $extra is "game", $value is "htr", and $expectedValue is the runner
				gr = (GameReady) actor;
				int ex = PHPHelper.toInt(expectedValue);
				ret = "";
				//Conversion note: switch instead of function from string
				switch (ex) {
					case 1:
						if (gr.getPlaySafeR1()) {
							ret = Integer.toString(ex);
						}
						break;
					case 2:
						if (gr.getPlaySafeR2()) {
							ret = Integer.toString(ex);
						}
						break;
					case 3:
						if (gr.getPlaySafeR3()) {
							ret = Integer.toString(ex);
						}
						break;

				}
				break;
			case "age":
				ret = avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_age");
				break;
			case "speed":
				ret = avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_speed");
				if ("S".equals(ret.toUpperCase())) {
					ret = "slow";
				} else if ("F".equals(ret.toUpperCase())) {
					ret = "fast";
				}
				break;
			case "wild":
				ret = avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_control");
				break;
			case "pb":
				ret = avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_pb");
				break;
			case "wp":
				ret = avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_wp");
				break;
			case "bk":
				ret = avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_bk");
				break;
			case "hb":
				ret = avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_hb");
				break;
			case "strikeout":
				ret = avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_strikeout");
				break;
			case "control":
				ret = avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_control");
				break;
			case "throws":
				ret = avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_throws");
				break;
			case "hits":
				ret = avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_hits");
				break;
			case "k":
			case "K":
				if (avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_strikeout").toLowerCase().indexOf("k") != -1) {
					ret = "true";
				} else {
					ret = "false";
				}
				break;
			case "r":
			case "R":
				if (avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_strikeout").toLowerCase().indexOf("r") != -1) {
					ret = "true";
				} else {
					ret = "false";
				}
				break;
			case "x":
			case "X":
				if (avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_strikeout").toLowerCase().indexOf("x") != -1) {
					ret = "true";
				} else {
					ret = "false";
				}
				break;
			case "y":
			case "Y":
				if (avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_strikeout").toLowerCase().indexOf("y") != -1) {
					ret = "true";
				} else {
					ret = "false";
				}
				break;
			case "pg": //Note: same as case "grade"
				String pg = "pg";
				if (((GameNpc) actor).isReliefPitcher()) {
					pg = "relief_pg";
				}
				ret = avatarRepository.getRes(((GameNpc) actor).getAvatar(), pg);
				break;
			case "w":
			case "W":
				if (avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_control").toLowerCase().indexOf("w") != -1) {
					ret = "true";
				} else {
					ret = "false";
				}
				break;
			case "zz":
			case "ZZ":
				if (avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_control").toLowerCase().indexOf("zz") != -1) {
					ret = "true";
				} else {
					ret = "false";
				}
				break;
			case "z":
			case "Z":
				if (avatarRepository.getStat(((GameNpc) actor).getAvatar(), "cd_control").toLowerCase().indexOf("z") != -1) {
					ret = "true";
				} else {
					ret = "false";
				}
				break;
			case "resultcolumn":
				ret = ((Integer) game.get(RESULT_COLUMN)).toString();
				break;
			default:
				//Conversion note: throw new RuntimeException here to find out all possible cases
				ret = getReflectedValue(actor, value);
				break;
		}

		return ret == null ? null : ret.toLowerCase();
	}

	private String getReflectedValue(Object actor, String value) {
		String methodName = "get" + StringUtils.capitalize(value);

		try {
			Method method = actor.getClass().getMethod(methodName);
			Object o = method.invoke(actor);
			return o == null ? null : o.toString();
		} catch (Exception e) {
			log.error("Actor class: " + actor.getClass().toString());
			log.error("value: " + value);
			log.error("methodName: " + methodName);
			throw new RuntimeException("Unexpected getTypeCommand parameters!");
		}
	}
	
	/**
	 * 
	 * @param target - GameReady object
	 * @param value - name of field
	 * @return invocation result converted to int
	 */
	private int getReflectedIntValue(GameReady target, String value) {
		//Note: GameReady boolean getter names start with "get", not "is"
		String methodName = "get" + StringUtils.capitalize(value);

		try {
			Method method = target.getClass().getMethod(methodName);
			Object o = method.invoke(target);
			//Note: Integer.class.isInstance and Boolean.class.isInstance also works for primitive types
			if (Integer.class.isInstance(o)) {
				return PHPHelper.toInt((int) o);
			} else if (String.class.isInstance(o)) {
				return PHPHelper.toInt((String) o);
			} else if (Boolean.class.isInstance(o)) {
				return PHPHelper.toInt((boolean) o);
			}
			throw new RuntimeException("Not supported getReflectedIntValue class returned by method: " + methodName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Calls setter with conversion to string or boolean if needed
	 * @param target
	 * @param name - name of field
	 * @param amt
	 */
	private void setReflected(Object target, String name, int amt) {
		String methodName = "set" + StringUtils.capitalize(name);
		Method method = ReflectionUtils.findMethod(target.getClass(), methodName, null);
		if (method == null) {
			throw new RuntimeException("Not found method " + target.getClass().getName() + "." + methodName);
		}

		Object param = null;
		String paramClass = method.getParameterTypes()[0].getName();
		
		if (paramClass.equals(int.class.getName()) || paramClass.equals(Integer.class.getName())) {
			param = amt;
		} else if (paramClass.equals(boolean.class.getName()) || paramClass.equals(Boolean.class.getName())) {
			param = amt == 0 ? false : true;
		} else if (paramClass.equals(String.class.getName())) {
			param = Integer.toString(amt);
		}
		
		if (param == null) {
			throw new RuntimeException("Unsupported parameter class " + paramClass + " for method " + target.getClass().getName() + "." + methodName);
		}
		try {
			method.invoke(target, param);
		} catch (Exception e) {
			log.error("setReflected method invocation failed");
			throw new RuntimeException(e);
		}
	}

	private void updateStat(Map<String, Object> game, String position, String stat) {
		updateStat(game, position, stat, 1);
	}

	private void updateStat(Map<String, Object> game, String position, String stat, int amt) {
		//Conversion note: positions already implemented in GameService.translate (with lower case)
		updateStat((GameNpc) game.get(GameService.translate(position)), stat, amt);
	}

	public void updateGame(Map<String, Object> game, String stat, int amt) {
		GameReady gr = (GameReady) game.get("game");
		if (gr.getInningTop()) {
			if ("Hits".equals(stat)) {
				gr.setAwayHits(gr.getAwayHits() + amt);
			} else if ("Errors".equals(stat)) {
				gr.setHomeErrors(gr.getHomeErrors() + amt);
			} else if ("Balls".equals(stat)) {
				gr.setBalls(gr.getBalls() + amt);
			} else if ("Strikes".equals(stat)) {
				gr.setStrikes(gr.getStrikes() + amt);
			} else {
				setReflected(gr, stat, getReflectedIntValue(gr, stat) + amt);
			}
		} else {
			if ("Errors".equals(stat)) {
				gr.setAwayErrors(gr.getAwayErrors() + amt);
			} else if ("Hits".equals(stat)) {
				gr.setHomeHits(gr.getHomeHits() + amt);
			} else if ("Balls".equals(stat)) {
				gr.setBalls(gr.getBalls() + amt);
			} else if ("Strikes".equals(stat)) {
				gr.setStrikes(gr.getStrikes() + amt);
			} else {
				setReflected(gr, stat, getReflectedIntValue(gr, stat) + amt);
			}
		}
		entityManager.persist(gr);
	}

	public void moveRunner(Map<String, Object> game, String position, int distance) {
		//Update the position
		Map<String, Integer> moves = (Map<String, Integer>) game.get("moves");
		moves.put(position, (moves.containsKey(position) ? moves.get(position) : 0) + distance);
	}

	private void trackErrorOrOut(Movements move, String position) {
		move.delayedOutTracking.add(position);
	}

	/**
	 * Should only be used after processing all other earned run scenarios
	 *
	 * @param position parser position name (e.g. 'bt')
	 */
	private void recordErrorOrOut(Map<String, Object> gameMap, String position) {
		logEarnedMoveInfo("recordErrorOrOut begin  " + position);

		GameReady gameready = (GameReady) gameMap.get("game");

		GameNpc currentPitcher = (GameNpc) gameMap.get("pitcher");
		int currentPitcherId = currentPitcher.getId();

		Map<Integer, Integer> pitcherOuts = new LinkedHashMap<>();
		getOrFindAndUpdatePitcherErrorsAndOuts(gameready, pitcherOuts, currentPitcherId);

		AvatarStat runnersStat = getEarnedRunners(gameMap);
		List<Map<String, Object>> runners = getRunnerStats(runnersStat);
		logEarnedMoveInfo("recordErrorOrOut runners ", runners);

		// If not one of these positions, it is a fielding error
		if (POSITION_BATTER.equals(position) || POSITION_RUNNER1.equals(position) ||
				POSITION_RUNNER2.equals(position) || POSITION_RUNNER3.equals(position)) {

			//Conversion note: getGamePositionKey already implemented in GameService
			GameNpc player = (GameNpc) gameMap.get(GameService.translate(position));
			int playerIndex = getOrCreateRunnerIndex(runners, player.getId(), player.getFullName(), currentPitcherId);
			Map<String, Object> playerRunner = runners.get(playerIndex);

			if ((boolean) playerRunner.get(RUNNERS_NEW)) {
				writeNewEarnedRunnerToConsole(gameready, playerRunner);
				playerRunner.put(RUNNERS_NEW, false);
				logEarnedMoveInfo("recordErrorOrOut new  ", playerRunner);
				
				// No more tracking - the player is out.
				// We could remove the entry at this point but currently storing for debug purposes
				playerRunner.put(RUNNERS_ACTIVE, false);
				playerRunner.put(RUNNERS_ALWAYS_UNEARNED, true);
			}
		}

		// Check all other runners to see if they can no longer be considered for an earned run
		for (int i = 0; i < runners.size(); i++) {
			Map<String, Object> runner = runners.get(i);

			if (canBeConsideredForEarnedRuns(runner)) {
				int errorsAndOuts = getOrFindAndUpdatePitcherErrorsAndOuts(gameready, pitcherOuts, (int) runner.get(RUNNERS_PITCHER_ID));
				// No more tracking - the player can't be considered for an earned run anymore
				// We could remove the entry at this point but currently storing for debug purposes
				if (errorsAndOuts >= 3) {
					runner.put(RUNNERS_ALWAYS_UNEARNED, true);
					runner.put(RUNNERS_ACTIVE, false);
				}
			}
		}

		saveEarnedRunners(gameready, runnersStat, runners);
		logEarnedMoveInfo("recordErrorOrOut end  ", runners);
	}

	private void trackUnearnedMove(Map<String, Object> game, Movements move, String position, int distance, String type) {
		if (ACTION_UNEARNED_PRIORITY.equals(type)) {
			recordUnearnedMove(game, position, distance);
		} else {
			List<Object> savedInformation = new ArrayList<>();
			savedInformation.add(position);
			savedInformation.add(distance);
			move.delayedUnearnedMoveTracking.add(savedInformation);
		}
	}

	private void recordUnearnedMove(Map<String, Object> gameMap, String position, int distance) {
		logEarnedMoveInfo("trackUnearnedMove begin  " + position + " " + distance);
		GameReady gameready = (GameReady) gameMap.get("game");

		AvatarStat runnersStat = getEarnedRunners(gameMap);
		List<Map<String, Object>> runners = getRunnerStats(runnersStat);
		logEarnedMoveInfo("trackUnearnedMove runners ", runners);
		
		GameNpc currentPitcher = (GameNpc) gameMap.get("pitcher");
		int currentPitcherId = currentPitcher.getId();
		//Conversion note: getGamePositionKey already implemented in GameService
		GameNpc player = (GameNpc) gameMap.get(GameService.translate(position));
		int playerIndex = getOrCreateRunnerIndex(runners, player.getId(), player.getFullName(), currentPitcherId);
		Map<String, Object> playerRunner = runners.get(playerIndex);


		// Technically - this should be if position is batter - but the new check is essentially the same difference
		// in this case. The scenario is that if a person gets on base first with an unearned run, he can never
		// be considered and earned run
		if ((boolean) playerRunner.get(RUNNERS_NEW)) {
			playerRunner.put(RUNNERS_ALWAYS_UNEARNED, true);
			playerRunner.put(RUNNERS_NEW, false);
			// Note that he is not inactive - we need to keep him here until he scores or is out, otherwise he
			// will end up being re-added
			writeNewEarnedRunnerToConsole(gameready, playerRunner);
			logEarnedMoveInfo("trackUnearnedMove new  ", playerRunner);
		}
		playerRunner.put(RUNNERS_UNEARNED_MOVE, (int) playerRunner.get(RUNNERS_UNEARNED_MOVE) + distance);
		saveEarnedRunners(gameready, runnersStat, runners);
		logEarnedMoveInfo("trackUnearnedMove end  ", runners);
	}

	private void trackEarnedMove(Map<String, Object> game, Movements move, String position, int distance) {
		trackEarnedMove(game, move, position, distance, "hit");
	}

	private void trackEarnedMove(Map<String, Object> game, Movements move, String position, int distance, String type) {
		if (ACTION_PRIORITY_MOVE.equals(type)) {
			move.priorityTracking.add(position);
			recordEarnedMove(game, move, position, distance, type);
		} else {
			List<Object> savedInformation = new ArrayList<>();
			savedInformation.add(position);
			savedInformation.add(distance);
			savedInformation.add(type);

			move.delayedMoveTracking.add(savedInformation);
		}
	}

	public void recordEarnedMove(Map<String, Object> game, Movements move, String position, int distance) {
		recordEarnedMove(game, move, position, distance, "hit");
	}

	public void recordEarnedMove(Map<String, Object> gameMap, Movements move, String position, int distance, String type) {
		logEarnedMoveInfo("trackEarnedMove begin  " + position + " " + distance + " " + type);
		GameReady gameready = (GameReady) gameMap.get("game");

		GameNpc currentPitcher = (GameNpc) gameMap.get("pitcher");
		int currentPitcherId = currentPitcher.getId();

		int currentPitcherInningsPartsPlayed = PHPHelper.toInt(avatarRepository.getRes(currentPitcher.getAvatar(), "ip"));
		int currentPitcherInningsPlayed = (int) (currentPitcherInningsPartsPlayed / 3.0);// Innings Played raw value refers to how many outs of the inning the pitcher played - each inning = 3 outs

		//Conversion note: getEarnedRunners already implemented
		AvatarStat runnersStat = getEarnedRunners(gameMap);
		String earned_out_tracking = avatarRepository.getStat(((GameNpc) gameMap.get("team")).getAvatar(), "earned_out_tracking");
		List<Map<String, Object>> runners = null;
		Map<String, Map<String, Integer>> tracking = null;
		try {
			runners = JSONUtil.toObject(runnersStat.getStart());
			tracking = "[]".equals(earned_out_tracking) ? new LinkedHashMap<>() : JSONUtil.toObject(earned_out_tracking);
		} catch (IOException e) {
			log.error("JSONUtil.toObject deserialization error", e);
			throw new RuntimeException(e);
		}
		logEarnedMoveInfo("trackEarnedMove runners ", runners);

		GameNpc player = null;
		Map<String, Object> playerRunner = null;

		if (!EARNED_RUN_REASSESS.equals(type)) {
			//Conversion note: getGamePositionKey already implemented in GameService
			player = (GameNpc) gameMap.get(GameService.translate(position));
			int playerIndex = getOrCreateRunnerIndex(runners, player.getId(), player.getFullName(), currentPitcherId);
			playerRunner = runners.get(playerIndex);

			if ((boolean) playerRunner.get(RUNNERS_NEW)) {
				writeNewEarnedRunnerToConsole(gameready, playerRunner);
				logEarnedMoveInfo("trackEarnedMove new  ", playerRunner);
				playerRunner.put(RUNNERS_NEW, false);
			} else if ((int) playerRunner.get(RUNNERS_EARNED_MOVE) + (int) playerRunner.get(RUNNERS_UNEARNED_MOVE) > 4) {
				// trap for invalid player, if they already passed home, they shouldn't continue to be modified
				playerRunner.put(RUNNERS_ACTIVE, false);
				playerIndex = getOrCreateRunnerIndex(runners, player.getId(), player.getFullName(), currentPitcherId);
				playerRunner = runners.get(playerIndex);
				writeNewEarnedRunnerToConsole(gameready, playerRunner);
				logEarnedMoveInfo("trackEarnedMove new 2 ", playerRunner);
				playerRunner.put(RUNNERS_NEW, false);
			}
		}

		Map<Integer, GameNpc> pitcherNpcs = new LinkedHashMap<>();
		pitcherNpcs.put(currentPitcherId, currentPitcher);

		for (int i = runners.size() - 1; i >= 0; i--) { // need to go backward in case of rippling pushes for the reassess
			Map<String, Object> runner = runners.get(i);
			if (canBeConsideredForEarnedRuns(runner)) {

				// update earned MOVES
				if (EARNED_RUN_REASSESS.equals(type)) {
					// check following players to see if one pushed us up
					int effectiveBase = effectiveBase(runner);
					for (int j = i + 1; j < runners.size(); j++) {
						Map<String, Object> followingRunner = runners.get(j);
						int followingRunnerEffectiveBase = effectiveBase(followingRunner);

						if (followingRunnerEffectiveBase >= effectiveBase) {
							runner.put(RUNNERS_BATTER_EARNED_MOVE, (int) runner.get(RUNNERS_BATTER_EARNED_MOVE)
									+ followingRunnerEffectiveBase - effectiveBase + 1);
							break;
						}
					}
				} else if ((int) runner.get(RUNNERS_ID) == player.getId()) {
					runner.put(RUNNERS_EARNED_MOVE, (int) runner.get(RUNNERS_EARNED_MOVE) + distance);
				} else if (isEligibleForABatterEarnedMove(gameMap, move, position, runner)) {
					// If the batter earned moves, that translates to the players that already passed
					// (e.g. they would have earned the move anyways)
					runner.put(RUNNERS_BATTER_EARNED_MOVE, (int) runner.get(RUNNERS_BATTER_EARNED_MOVE) + distance);
				}

				// update earned RUNS
				if (hasRunnerEarnedARun(runner)) {
					int pitcherId = (int) runner.get(RUNNERS_PITCHER_ID);
					GameNpc pitcher = getOrFindPitcherNpc(pitcherNpcs, pitcherId);
					Avatar pitcherAvatar = pitcher.getAvatar();
					gameConsoleUtil.writeToConsole(gameready, pitcher.getFullName() + " has allowed an EARNED RUN (" + getLoggableType(type) + ") by " + runner.get(RUNNERS_NAME) + "!", MESSAGE_DEBUG);
					incrementPitcherEarnedRuns(gameready, pitcher);

					// At this point we could remove the entry but are keeping it for debugging purposes
					runner.put(RUNNERS_ACTIVE, false);
					runner.put(RUNNERS_EARNED_RUN, true);

					logEarnedMoveInfo("################ New earned run");

					// PHPTODO - this is probably wrong (existing behavior) - why would you update this pitcher in the currentInningsPlayed?
					incrementEarnedRunInInningsTracking(tracking, pitcherId, currentPitcherInningsPlayed);

					avatarRepository.setRes(pitcherAvatar, "pitcher_erb", avatarRepository.getRes(pitcherAvatar, "ip"));
				}

				// A player who could never be an earned runner passed home - inactivate him
				if (((int) runner.get(RUNNERS_EARNED_MOVE) + (int) runner.get(RUNNERS_UNEARNED_MOVE) > 4) && (boolean) runner.get(RUNNERS_ALWAYS_UNEARNED)) {
					// Again, we could remove the entry but are maintaining for debug purposes
					runner.put(RUNNERS_ACTIVE, false);
				}
			}
		}

		saveEarnedRunners(gameready, runnersStat, runners);
		try {
			gameConsoleUtil.writeToConsole(gameready, "Earned Run Information: " + JSONUtil.toMinifiedJSON(tracking), MESSAGE_TRACE);
		} catch (JsonProcessingException e) {
			//swallow exception
			log.error("JSONUtil.toMinifiedJSON serialization error", e);
		}

		for (Entry<String, Map<String, Integer>> entry : tracking.entrySet()) {
			int ers = 0;
			for (int i = 0; i < 3; i++) {
				ers += getAllowedEarnedRunsByPitcherAndInningsAgo(entry, currentPitcherId, currentPitcherInningsPlayed, i);
			}

			gameConsoleUtil.writeToConsole(gameready, "Earned Runs in the last 3 innings: " + ers, MESSAGE_TRACE);

			if (move.optionalPitchingRule && (PHPHelper.toInt(avatarRepository.getRes(currentPitcher.getAvatar(), "grade_modifier")) < 100)
					&& (ers >= 5) && (PHPHelper.toInt(avatarRepository.getRes(currentPitcher.getAvatar(), "pg")) > 3)) {

				while (ers >= 5) {
					int x = 5;
					for (int i = 0; i < 3; i++) {
						Integer y = currentPitcherInningsPlayed - i;
						if (entry.getValue().get(y.toString()) != null) {
							if (PHPHelper.toInt(entry.getValue().get(y.toString())) > x) {
								entry.getValue().put(y.toString(), entry.getValue().get(y.toString()) - x);
								x = 0;
							} else {
								x -= entry.getValue().get(y.toString());
								entry.getValue().put(y.toString(), 0);
							}
						}
					}
					ers -= 5;
					updateStat(gameMap, "p", "grade_modifier", -1);
					updateStat(gameMap, "p", "pg", -1);
				}

				if (PHPHelper.toInt(avatarRepository.getRes(currentPitcher.getAvatar(), "pg")) < 4) {
					avatarRepository.setRes(currentPitcher.getAvatar(), "pg", 4);
				} else {
					gameConsoleUtil.writeToConsole(gameready, ((GameNpc) gameMap.get("pitcher")).getFullName() + " has been reduced in grade!");
				}

				entityManager.persist(currentPitcher.getAvatar());
				//writing to iterated map may be not safe. And changes should be saved anyway
			}
		}
		
		try {
			avatarRepository.setStat(((GameNpc) gameMap.get("team")).getAvatar(), "earned_out_tracking", JSONUtil.toMinifiedJSON(tracking));
		} catch (JsonProcessingException e) {
			log.error("JSONUtil.toMinifiedJSON serialization error", e);
			throw new RuntimeException(e);
		}
		
		entityManager.persist(((GameNpc) gameMap.get("team")).getAvatar());

		logEarnedMoveInfo("trackEarnedMove end  ", runners);
	}

	private int getAllowedEarnedRunsByPitcherAndInningsAgo(Entry<String, Map<String, Integer>> earnedRunsTrackingEntry, int pitcherId, 
			int pitcherInningsPlayed, int inningsAgo) {

		int result = 0;
		if (earnedRunsTrackingEntry.getKey().equals(Integer.toString(pitcherId))) {
			if (earnedRunsTrackingEntry.getValue().get(Integer.toString(pitcherInningsPlayed - inningsAgo)) != null) {
				result += earnedRunsTrackingEntry.getValue().get(Integer.toString(pitcherInningsPlayed - inningsAgo));
			}
		}
		return result;
	}

	private void writeNewEarnedRunnerToConsole(GameReady gameready, Map<String, Object> runner) {
		gameConsoleUtil.writeToConsole(gameready, "New Earned Runner: " + runner.get(RUNNERS_ID) + " " + runner.get(RUNNERS_NAME), MESSAGE_TRACE);
	}

	private boolean isEligibleForABatterEarnedMove(Map<String, Object> game, Movements move, String position, Map<String, Object> runner) {
		return POSITION_BATTER.equals(position) && ((int) runner.get(RUNNERS_UNEARNED_MOVE) > 0) && !move.batterMovedOnBalls && !runnerEarnedAMoveThisPlay(game, runner, move);
	}

	private boolean runnerEarnedAMoveThisPlay(Map<String, Object> game, Map<String, Object> runner, Movements move) {

		if (!isRunnerStillOnBase(runner)) {
			logEarnedMoveInfo(runner.get(RUNNERS_NAME) + " is not on base");
			return false;
		}

		String position = getRunnerPosition(game, runner);

		if (position == null) {
			logEarnedMoveInfo(runner.get(RUNNERS_NAME) + " is not on base - no position");
			return false;
		}

		for (List<Object> moveInfo : move.delayedMoveTracking) {
			if (position.equals(moveInfo.get(0)) && ((int) moveInfo.get(1) > 0)) {
				logEarnedMoveInfo(runner.get(RUNNERS_NAME) + " had an earned run this play");
				return true;
			}
		}

		for (String priorityPosition : move.priorityTracking) {
			if (position.equals(priorityPosition)) {
				logEarnedMoveInfo(runner.get(RUNNERS_NAME) + " had an earned run this play (2)");
				return true;
			}
		}

		return false;
	}

	private boolean isRunnerStillOnBase(Map<String, Object> runner) {
		return effectiveBase(runner) < 4;
	}

	private String getRunnerPosition(Map<String, Object> game, Map<String, Object> runner) {
		String[] runnerPositions = {POSITION_RUNNER1, POSITION_RUNNER2, POSITION_RUNNER3};

		for (String runnerPosition : runnerPositions) {
			if (isRunnerAtPosition(game, runner, runnerPosition)) {
				return runnerPosition;
			}
		}

		return null;
	}

	/**
	 * @param runner
	 * @param parserPosition
	 * @return bool
	 */
	private boolean isRunnerAtPosition(Map<String, Object> game, Map<String, Object> runner, String parserPosition) {
		GameNpc player = (GameNpc) game.get(GameService.translate(parserPosition));
		return (player != null) && (player.getId() == runner.get(RUNNERS_ID));
	}

	/**
	 * @param pitcherNpcs
	 * @param pitcherId
	 * @return GameNpc
	 */
	private GameNpc getOrFindPitcherNpc(Map<Integer, GameNpc> pitcherNpcs, int pitcherId) {
		if (!pitcherNpcs.containsKey(pitcherId)) {
			GameNpc pitcher = gameNpcRepository.findById(pitcherId).get();
			pitcherNpcs.put(pitcherId, pitcher);
		}
		return pitcherNpcs.get(pitcherId);
	}

	/**
	 * Note: DOES NOT FLUSH
	 *
	 * @param pitcher
	 */
	private void incrementPitcherEarnedRuns(GameReady gameReady, GameNpc pitcher) {
		updateStat(pitcher, "er", 1);
	}

	private void updateStat(GameNpc npc, String stat, int amount) {
		TimerUtil updateStatTimer = new TimerUtil("updateStatTimer");
		AvatarRes res = avatarRepository.loadRes(npc.getAvatar(), stat);
		if (res == null) {
			res = new AvatarRes();
			TimerUtil setclientTimer = new TimerUtil("setclientTimer"); 
			//res.setClient(npc.getAvatar().getClient());
			setclientTimer.endTimer();
			TimerUtil setclientTimer1 = new TimerUtil("setclientTimer1"); 
			res.setClient(ClientUtil.client);
			setclientTimer1.endTimer();
			res.setAvatar(npc.getAvatar());
			res.setRes(ResRepositoryUtil.getRes(stat));
			res.setStart(0);
			
		}
		res.setStart(PHPHelper.toInt(res.getStart()) + amount);
		entityManager.persist(res);
		updateStatTimer.endTimer();
	}

	private void incrementEarnedRunInInningsTracking(Map<String, Map<String, Integer>> tracking, Integer pitcherId, Integer currentPitcherInningsPlayed) {

		if (tracking.get(pitcherId.toString()) == null) {
			tracking.put(pitcherId.toString(), new LinkedHashMap<>());
		}

		if (tracking.get(pitcherId.toString()).get(currentPitcherInningsPlayed.toString()) == null) {
			tracking.get(pitcherId.toString()).put(currentPitcherInningsPlayed.toString(), 0);
		}
		tracking.get(pitcherId.toString()).put(currentPitcherInningsPlayed.toString(), tracking.get(pitcherId.toString()).get(currentPitcherInningsPlayed.toString()) + 1);
	}

	/**
	 * @param runnersStat
	 * @param runners
	 */
	public void saveEarnedRunners(GameReady gameReady, AvatarStat runnersStat, List<Map<String, Object>> runners) {
		try {
			runnersStat.setStart(JSONUtil.toMinifiedJSON(runners));
		} catch (JsonProcessingException e) {
			log.error("saveEarnedRunners serialization error");
			throw new RuntimeException(e);
		}
		entityManager.persist(runnersStat);
	}

	/**
	 * @param game
	 * @return AvatarStat
	 */
	public AvatarStat getEarnedRunners(Map<String, Object> game) {
		return avatarRepository.loadStat(((GameNpc) game.get("team")).getAvatar(), "earned_runners");
	}

	/**
	 * Returns index of active runner with specified playerID or -1
	 * @param runners
	 * @param playerId
	 * @return
	 */
	public int getRunnerIndex(List<Map<String, Object>> runners, int playerId) {
		for (int i = 0; i < runners.size(); i++) {
			if ((boolean) runners.get(i).get(RUNNERS_ACTIVE) && ((int) runners.get(i).get(RUNNERS_ID) == playerId)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * @param runners
	 * @param playerId
	 * @param playerName
	 * @param currentPitcherId
	 * @return int The index the runner is found at
	 */
	public int getOrCreateRunnerIndex(List<Map<String, Object>> runners, int playerId, String playerName, int currentPitcherId) {
		int i = getRunnerIndex(runners, playerId);
		if (i != -1) {
			return i;
		}

		Map<String, Object> runner = new LinkedHashMap<>();
		runner.put(RUNNERS_ACTIVE, true);
		runner.put(RUNNERS_ID, playerId);
		runner.put(RUNNERS_NAME, playerName);
		runner.put(RUNNERS_PITCHER_ID, currentPitcherId);
		runner.put(RUNNERS_UNEARNED_MOVE, 0);// The number of unearned moves this player makes
		runner.put(RUNNERS_EARNED_MOVE, 0);// The number of earned moves this player makes
		runner.put(RUNNERS_BATTER_EARNED_MOVE, 0);// The number of earned moves the FOLLOWING batters make
		runner.put(RUNNERS_ALWAYS_UNEARNED, false);// Marked true if the player can never be an earned run (was unearned onto first or errors+outs >= 3)
		runner.put(RUNNERS_EARNED_RUN, false);// Marked true if the player has earned a run - active will be set to false
		runner.put(RUNNERS_NEW, true);// Marked to indicate this is a new player - will be removed almost immediately

		runners.add(runner);

		return runners.size() - 1;
	}

	private boolean canBeConsideredForEarnedRuns(Map<String, Object> runner) {
		return (boolean) runner.get(RUNNERS_ACTIVE) && !(boolean) runner.get(RUNNERS_ALWAYS_UNEARNED) && !(boolean) runner.get(RUNNERS_EARNED_RUN);
	}

	private boolean hasRunnerEarnedARun(Map<String, Object> runner) {
		// Note: we handle errors and outs in a separate function and mark players unable to achieve that
		// there so we don't retest here
		return effectiveBase(runner) >= 4;
	}

	private int effectiveBase(Map<String, Object> runner) {
		return (int) runner.get(RUNNERS_UNEARNED_MOVE) == 0 ?
				(int) runner.get(RUNNERS_EARNED_MOVE)
				: ((int) runner.get(RUNNERS_EARNED_MOVE) + (int) runner.get(RUNNERS_BATTER_EARNED_MOVE));
	}

	/**
	 * For a pitcherId that doesn't exist in pitcherOuts, finds the earned_outs stat, increments it, saves it, adds
	 * it to pitcherOuts and returns it.
	 * <p>
	 * If it already exists in pitcherOuts, returns it.
	 *
	 * @param pitcherOuts
	 * @param pitcherId
	 * @return int The count of eroors and outs
	 */
	private int getOrFindAndUpdatePitcherErrorsAndOuts(GameReady gameReady, Map<Integer, Integer> pitcherOuts, int pitcherId) {
		if (!pitcherOuts.containsKey(pitcherId)) {
			pitcherOuts.put(pitcherId, incrementPitcherErrorsAndOuts(gameReady, pitcherId));
		}
		return pitcherOuts.get(pitcherId);
	}

	/**
	 * Finds the earned_outs stat, increments it, saves it, returns the value of errors+outs
	 *
	 * @param pitcherId The pitcher GameNpc.Id to lookup
	 * @return int the errors+outs value
	 */
	private int incrementPitcherErrorsAndOuts(GameReady gameReady, int pitcherId) {

		GameNpc pitcher = gameNpcRepository.findById(pitcherId).get();
		AvatarStat earnedOutStat = avatarRepository.loadStat(pitcher.getAvatar(), "earned_outs");
		Integer errorsAndOuts = PHPHelper.toInt(earnedOutStat.getStart()) + 1;

		earnedOutStat.setStart(errorsAndOuts.toString());
		entityManager.persist(earnedOutStat);
		logEarnedMoveInfo("Errors and outs (" + pitcherId + "): " + errorsAndOuts);
		return errorsAndOuts;
	}

	private String getLoggableType(String type) {
		String earnedRunType = "virtual/s/d/t/h";
		if (ACTION_PRIORITY_MOVE.equals(type)) {
			earnedRunType = "priority";
		} else if (ACTION_MOVE.equals(type)) {
			earnedRunType = "move";
		}
		return earnedRunType;
	}

	private void logEarnedMoveInfo(String s) {
		if (LOG_EXCESSIVE_EARNED_MOVES) {
			log.error(s);
		}
	}
	
	private void logEarnedMoveInfo(String s, Object c) {
		try {
			logEarnedMoveInfo(s + JSONUtil.toMinifiedJSON(c));
		} catch (JsonProcessingException e) {
			//swallow exception
			log.error("JSONUtil.toMinifiedJSON serialization error", e);
		}
	}

	public List<Map<String, Object>> getRunnerStats(AvatarStat runnersStat) {
		List<Map<String, Object>> runners = null;
		try {
			runners = JSONUtil.toObject(runnersStat.getStart());
		} catch (IOException e) {
			log.error("JSONUtil.toObject deserialization error", e);
			throw new RuntimeException(e);
		}
		return runners;
	}
}

/**
 * Class to hold request-scoped values for APBAParserService
 */
class Movements {
	List<String> delayedOutTracking = new ArrayList<>();
	List<List<Object>> delayedMoveTracking = new ArrayList<>();
	List<List<Object>> delayedUnearnedMoveTracking = new ArrayList<>();
	List<String> priorityTracking = new ArrayList<>();
	boolean batterMovedOnBalls = false;
	boolean optionalPitchingRule;
	boolean optionalInjury;
	boolean optionalRain;
}



