package com.lixar.apba.service;

import com.lixar.apba.core.util.*;
import com.lixar.apba.domain.GameLineup;
import com.lixar.apba.domain.GameNpc;
import com.lixar.apba.domain.GameNpcStat;
import com.lixar.apba.domain.GameReady;
import com.lixar.apba.domain.Stat;
import java.io.IOException;
import java.util.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;

import com.lixar.apba.core.book.validator.OutcomeValidatorChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lixar.apba.repository.AvatarRepository;
import com.lixar.apba.repository.GameLineupRepository;
import com.lixar.apba.repository.GameNpcRepository;
import com.lixar.apba.repository.GameReadyRepository;
import com.lixar.apba.repository.GameSectorRepository;
import com.lixar.apba.repository.StatRepository;
import com.lixar.apba.repository.util.StatRepositoryUtil;
import com.lixar.apba.service.util.AutoplayUtil;
import com.lixar.apba.service.util.EntityManagerUtil;
import com.lixar.apba.service.util.TimerUtil;
import com.lixar.apba.web.SessionKeys;
import com.lixar.apba.web.rest.util.GameConsoleUtil;

import static com.lixar.apba.web.SessionKeys.*;

@Service
public class AISimpletonService extends MicroManagerService {

    @Inject
	private GameLineupRepository gameLineupRepository;

    @Inject
	private GameConsoleUtil gameConsoleUtil;
    
	@Inject
	private GameReadyRepository gameReadyRepository;

    @Inject
    private GameNpcRepository gameNpcRepository;

    @Inject
    private AvatarRepository avatarRepository;

    @Inject
    private GameSectorRepository gameSectorRepository;

    @Inject
    private NpcService npcService;

    @Inject
    private EndGameService endGameService;

    @Inject
    private GameStatusService gameStatusService;

    @PersistenceContext
    private EntityManager entityManager;
    
    @Inject
    private EntityManagerUtil entityManagerUtil;
    
    @Inject
    private StatRepository statRepository;

    @Transactional
    public String batting(HttpSession session, Map<String, Object> game) throws IOException {

        Integer gid = (Integer) session.getAttribute(GAME);

        GameReady gameready = gameReadyRepository.findOneByGame(gid);
        GameNpc batter = (GameNpc) game.get("batter");
        GameNpc runner1 = (GameNpc) game.get("runner1");
        GameNpc runner2 = (GameNpc) game.get("runner2");
        GameNpc runner3 = (GameNpc) game.get("runner3");
        Stat combineStat = StatRepositoryUtil.getStat("combine");

        //Condition: Pitcher comes up to bat
        //Conversion note: comparing ids instead of entities
        if (ObjectUtil.areEqualIds(getAiPitcher(session, gameready), batter)) {
            if (gameready.getInning() >= 6 && gameready.getAiSub(isPlayingHome(session, gameready)) == GameReady.REPLACEMENT_UNNECESSARY) {
                //Pinch Hitter
                Query query = query(session, "pinch", combineStat);
                List<GameNpc> relief_list = query.getResultList();
                GameNpc replacement = null;
                // Select a replacement for the batting pitcher. First non-pitcher in the $relief_list is chosen
                for (GameNpc n : relief_list) {
                    if (!StringUtil.phpStrToBool(avatarRepository.getCachedRes(n.getAvatar(), "injury")) &&
                        !StringUtil.phpStrToBool(gameNpcRepository.getStat(n, "position")) &&
                        !n.isBenched()) {

                        String eligibility = avatarRepository.getStat(n.getAvatar(), "cd_epos");
                        LinkedHashMap<String, String> eligibilityObject = JSONUtil.toObject(eligibility);
                        if (!eligibilityObject.containsKey("P")) {
                            replacement = n;
                            break;
                        }
                    }
                }
                if (replacement != null) {
                    npcService.swapPlayer(game, batter, replacement, "P", session, gameready, gameNpcRepository.getStat(batter, "batting"), false, isPlayingHome(session, gameready));
                    gameready.addReplacementMark(isPlayingHome(session, gameready), GameReady.REPLACEMENT_ADVICE);
                    entityManager.persist(gameready);
                    if (!AutoplayUtil.isAutoplayGame(gid)) {
                    	gameStatusService.markStatusChanged(session, false);
                    }
                }
            }
        }

		entityManagerUtil.flush(session);

        //Condition: Batting
        if ((runner1 != null) &&
            (gameready.getCso() == 0) &&
            (avatarRepository.getStat(runner1.getAvatar(), "cd_speed").toUpperCase().equals("F")) &&
            (runner2 == null) &&
            (runner3 == null) &&
            (PHPHelper.toDouble(avatarRepository.getStatUnfiltered(runner1.getAvatar(), "s_slg")) - PHPHelper.toDouble(avatarRepository.getStatUnfiltered(runner1.getAvatar(), "s_ba")) <= 0.2)) {
            return "hitrun";
        } else if ((gameready.getCso() == 0) &&
            (PHPHelper.toDouble(avatarRepository.getStatUnfiltered(batter.getAvatar(), "s_ops")) <= 0.65) &&
            ((runner1 != null) || (runner2 != null) || (runner3 != null))) {
            return "sacrifice";
        } else {
            return "normal";
        }
    }

    @Transactional
    // Converted from AISimpletonController->buildLineup - I have no idea what the following comment was supposed to mean
    /* XXX Not Done - shouldn't need changing */
    public void buildLineup(HttpSession session, int gameId, int opponentPlayerId) throws IOException {
        GameLineup lineup_json = gameLineupRepository.findOneByGameAndPlayer(gameId, opponentPlayerId);

        List<Map<String, Map<String, Object>>> lineup = JSONUtil.toObject(lineup_json.getLineup());
        GameReady gameready = gameReadyRepository.findOneByGame(gameId);

        int infield = 0;
        int team = 0;
        Map<String, Map<String, Object>> lineup0 = getDefaultLineup(lineup);
        for (String playerKey : lineup0.keySet()) {
            Map<String, Object> player = lineup0.get(playerKey);
            if (player.get("playerid") == null) {
                continue;
            }
            GameNpc npc = gameNpcRepository.findById((int) player.get("playerid")).get();
            Integer i = (Integer) player.get("batting");

            GameNpcStat npcstat = gameNpcRepository.loadStat(npc, "position");
            npcstat.setNpc(npc);
            npcstat.setStat(StatRepositoryUtil.getStat("position"));
            npcstat.setStart((String) player.get("pos"));
            entityManager.persist(npcstat);

            npcstat = gameNpcRepository.loadStat(npc, "past_position");
            npcstat.setNpc(npc);
            npcstat.setStat(StatRepositoryUtil.getStat("past_position"));
            npcstat.setStart((String) player.get("pos"));
            entityManager.persist(npcstat);

            npcstat = gameNpcRepository.loadStat(npc, "starting_player");
            npcstat.setNpc(npc);
            npcstat.setStat(StatRepositoryUtil.getStat("starting_player"));
            npcstat.setStart("1");
            entityManager.persist(npcstat);

            npcstat = gameNpcRepository.loadStat(npc, "rating");
            npcstat.setNpc(npc);
            npcstat.setStat(StatRepositoryUtil.getStat("rating"));
            npcstat.setStart("0");
            // Conversion note - this gets persisted way further down

            if (i == null) {
                i = 10;
            }

            if (i >= 1 && i <= 9) {
                gameready.setBatter(i, npc);

                GameNpcStat stat = gameNpcRepository.loadStat(npc, "batting");
                stat.setNpc(npc);
                stat.setStat(StatRepositoryUtil.getStat("batting"));
                stat.setStart(i.toString());
                entityManager.persist(stat);
            } else {
                GameNpcStat stat = gameNpcRepository.loadStat(npc, "batting");
                stat.setNpc(npc);
                stat.setStat(StatRepositoryUtil.getStat("batting"));
                stat.setStart("0");
                entityManager.persist(stat);
            }
            boolean isHomeTeam = APBAHelper.isPlayingHome(gameready, npc);
            switch ((String) player.get("pos")) {
                case "P":
                case "P*": //Pitcher
                    npcstat.setStart(avatarRepository.getStat(npc.getAvatar(), "pr"));
                    infield += PHPHelper.toInt(npcstat.getStart());
                    team += PHPHelper.toInt(npcstat.getStart());
                    if (isHomeTeam) {
                        gameready.setHomePitcher(npc);
                    } else {
                        gameready.setAwayPitcher(npc);
                    }
                    break;
                case CATCHER_SHORT: //Catcher
                    npcstat.setStart(avatarRepository.getStat(npc.getAvatar(), "cr"));
                    infield += PHPHelper.toInt(npcstat.getStart());
                    team += PHPHelper.toInt(npcstat.getStart());
                    if (isHomeTeam) {
                        gameready.setHomeCatcher(npc);
                    } else {
                        gameready.setAwayCatcher(npc);
                    }
                    break;
                case FIRST_BASE_SHORT: //1st Baseman
                    npcstat.setStart(avatarRepository.getStat(npc.getAvatar(), "1br"));
                    infield += PHPHelper.toInt(npcstat.getStart());
                    team += PHPHelper.toInt(npcstat.getStart());
                    if (isHomeTeam) {
                        gameready.setHomeFirst(npc);
                    } else {
                        gameready.setAwayFirst(npc);
                    }
                    break;
                case SECOND_BASE_SHORT: //2nd Baseman
                    npcstat.setStart(avatarRepository.getStat(npc.getAvatar(), "2br"));
                    infield += PHPHelper.toInt(npcstat.getStart());
                    team += PHPHelper.toInt(npcstat.getStart());
                    if (isHomeTeam) {
                        gameready.setHomeSecond(npc);
                    } else {
                        gameready.setAwaySecond(npc);
                    }
                    break;
                case THIRD_BASE_SHORT: //3rd Baseman
                    npcstat.setStart(avatarRepository.getStat(npc.getAvatar(), "3br"));
                    infield += PHPHelper.toInt(npcstat.getStart());
                    team += PHPHelper.toInt(npcstat.getStart());
                    if (isHomeTeam) {
                        gameready.setHomeThird(npc);
                    } else {
                        gameready.setAwayThird(npc);
                    }
                    break;
                case SHORT_STOP_SHORT: //Shortstop
                    npcstat.setStart(avatarRepository.getStat(npc.getAvatar(), "ssr"));
                    infield += PHPHelper.toInt(npcstat.getStart());
                    team += PHPHelper.toInt(npcstat.getStart());
                    if (isHomeTeam) {
                        gameready.setHomeShort(npc);
                    } else {
                        gameready.setAwayShort(npc);
                    }
                    break;
                case LEFT_FIELD_SHORT:
                    npcstat.setStart(avatarRepository.getStat(npc.getAvatar(), "ofr"));
                    team += PHPHelper.toInt(npcstat.getStart());
                    entityManager.persist(npcstat);
                    if (isHomeTeam) {
                        gameready.setHomeLeft(npc);
                    } else {
                        gameready.setAwayLeft(npc);
                    }
                    break;
                case CENTER_FIELD_SHORT:
                    npcstat.setStart(avatarRepository.getStat(npc.getAvatar(), "ofr"));
                    team += PHPHelper.toInt(npcstat.getStart());
                    entityManager.persist(npcstat);
                    if (isHomeTeam) {
                        gameready.setHomeCenter(npc);
                    } else {
                        gameready.setAwayCenter(npc);
                    }
                    break;
                case RIGHT_FIELD_SHORT:
                    npcstat.setStart(avatarRepository.getStat(npc.getAvatar(), "ofr"));
                    team += PHPHelper.toInt(npcstat.getStart());
                    entityManager.persist(npcstat);
                    if (isHomeTeam) {
                        gameready.setHomeRight(npc);
                    } else {
                        gameready.setAwayRight(npc);
                    }
                    break;
                case DESIGNATED_HITTER_SHORT: //Designated Hitter
                    npcstat.setStart("0");
                    entityManager.persist(npcstat);
                    if (isHomeTeam) {
                        gameready.setHomeHitter(npc);
                    } else {
                        gameready.setAwayHitter(npc);
                    }
                    break;
            }
            entityManager.persist(npcstat);
            entityManager.persist(gameready);
            
            GameNpc team_npc = gameNpcRepository.findByPlayerAndLastName(npc.getPlayer(), "team");
            GameNpc infield_npc = gameNpcRepository.findByPlayerAndLastName(npc.getPlayer(), "infield");

            npcstat = gameNpcRepository.loadStat(team_npc, "rating");
            npcstat.setNpc(team_npc);
            npcstat.setStat(StatRepositoryUtil.getStat("rating"));
            if (team >= 41) {
                npcstat.setStart("1");
            } else if (team < 36) {
                npcstat.setStart("3");
            } else {
                npcstat.setStart("2");
            }
            entityManager.persist(npcstat);
            npcstat = gameNpcRepository.loadStat(infield_npc, "rating");
            npcstat.setNpc(infield_npc);
            npcstat.setStat(StatRepositoryUtil.getStat("rating"));
            if (infield >= 35) {
                npcstat.setStart("1");
            } else if (infield < 30) {
                npcstat.setStart("3");
            } else {
                npcstat.setStart("2");
            }
            entityManager.persist(npcstat);
        }

        gameready = gameReadyRepository.findOneByGame(gameId);


        gameready.getBatter3().setSector(gameSectorRepository.findByName(gameId, 1, "dugouthome"));
        entityManager.persist(gameready.getBatter3());
        gameready.getBatter4().setSector(gameSectorRepository.findByName(gameId, 1, "dugouthome"));
        entityManager.persist(gameready.getBatter4());
        gameready.getBatter5().setSector(gameSectorRepository.findByName(gameId, 1, "dugouthome"));
        entityManager.persist(gameready.getBatter5());
        gameready.getBatter6().setSector(gameSectorRepository.findByName(gameId, 1, "dugouthome"));
        entityManager.persist(gameready.getBatter6());
        gameready.getBatter7().setSector(gameSectorRepository.findByName(gameId, 1, "dugouthome"));
        entityManager.persist(gameready.getBatter7());
        gameready.getBatter8().setSector(gameSectorRepository.findByName(gameId, 1, "dugouthome"));
        entityManager.persist(gameready.getBatter8());
        gameready.getBatter9().setSector(gameSectorRepository.findByName(gameId, 1, "dugouthome"));
        entityManager.persist(gameready.getBatter9());
        gameready.setBattingPosition(1);
        if(gameready.getHomeMicroManagerId() != null) {
            gameready.getBatter1().setSector(gameSectorRepository.findByName(gameId, 1, "dugouthome"));
            entityManager.persist(gameready.getBatter1());
            gameready.getBatter2().setSector(gameSectorRepository.findByName(gameId, 1, "dugouthome"));
            entityManager.persist(gameready.getBatter2());
            gameready.setHomeLineup(true);
        } else {
            gameready.getBatter1().setSector(gameSectorRepository.findByName(gameId, 1, "batter"));
            entityManager.persist(gameready.getBatter1());
            gameready.getBatter2().setSector(gameSectorRepository.findByName(gameId, 1, "ondeck"));
            entityManager.persist(gameready.getBatter2());
            gameready.setAwayLineup(true);
        }

        entityManager.persist(gameready);
		entityManagerUtil.flush(session);
    }

    private Map<String, Map<String, Object>> getDefaultLineup(List<Map<String, Map<String, Object>>> lineups) {
        for (Map<String, Map<String, Object>> lineup : lineups) {
            if (lineup.get("-1").get("lineupName").equals("Default Lineup")) {
                return lineup;
            }
        }
        return lineups.get(0);
    }

    private GameNpc pitcherReplacement(HttpSession session, Map<String, Object> game, GameNpc pitcher) {
        GameReady gameready = gameReadyRepository.findOneByGame((Integer) session.getAttribute(SessionKeys.GAME));
        Stat combineStat = StatRepositoryUtil.getStat("combine");
        
        if (combineStat == null) {
        	combineStat = statRepository.findOneByReference("combine");
        	StatRepositoryUtil.addStatToCache(combineStat);
        }
        
        int index;
        Query query;
        switch (gameready.getInning()) {
            // Empty cases without breaks account for conditions to execute the case with the break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                index = 2;
                break;
            case 8:
                index = 1;
                break;
            default:
                index = 0;
                break;
        }
        query = query(session, "replaceP", combineStat);
        List<GameNpc> relief_list = (List<GameNpc>) query.getResultList();

        List<GameNpc> reliefs = new ArrayList<>();
        // Check for relief pitchers before we go into non-pitchers.
        for (GameNpc npc : relief_list) {
            //Conversion note: was: if (!$repositoryAvatar->getRes($n->getAvatar(),'injury') && !$repositoryGameNpc->getStat($n,'position')) {
            if (isNpcInjuredOrBenched(npc)) {
                reliefs.add(npc);
            }
        }

        if (reliefs.size() == 0) {
            query = query(session, "noRelievers", combineStat);
            relief_list = (List<GameNpc>) query.getResultList();

            reliefs = findReplacementPitcher(relief_list, reliefs);
            if (reliefs.isEmpty() && npcService.isInjuredOrEjected(pitcher.getAvatar())) {
                forfeit(session, game);
            }
            if (reliefs.isEmpty()) return pitcher;
        }

        if (reliefs.size() <= index) index = reliefs.size() - 1;
        return reliefs.get(index);
    }

    public boolean isNpcInjuredOrBenched(GameNpc npc) {
        return !PHPHelper.isTrue(avatarRepository.getRes(npc.getAvatar(), "injury")) &&
            !PHPHelper.isTrue(gameNpcRepository.getStat(npc, "position")) &&
            !npc.isBenched();
    }

    public boolean isPlayingHome(HttpSession session, GameReady gameReady) {
            return APBAHelper.isAiPlayingHome(session, gameReady);
    }
    
	public String inningChange(HttpSession session, Map<String, Object> game) {
		String swapMessage = "";
		GameReady gameready = gameReadyRepository.findOneByGame((Integer) session.getAttribute(SessionKeys.GAME));
		// Conversion note : reworked to use lookup in GameReady
		final boolean inningTop = gameready.getInningTop();
		Map<String, Map<String, Object>> build_list = buildList(gameready, inningTop);
        GameNpc npc = getAiPitcher(session, gameready);
        boolean hasReplacement = false;
        if (gameready.hasAnyReplacementMark(isPlayingHome(session, gameready), GameReady.REPLACEMENT_ADVICE) || gameready.getInning() == 8 || gameready.getInning() == 9 || gameready.getInning() == 11) {
            hasReplacement = true;
            gameready.setSub(isPlayingHome(session, gameready), GameReady.REPLACEMENT_INJURED);
            entityManager.persist(gameready);
            GameNpc replacement = pitcherReplacement(session, game, npc);
            if (replacement.equals(npc) || replacement.isBenched()) {
                hasReplacement = false;
            }
            if (hasReplacement) {
                build_list.put("P", new LinkedHashMap<>());
                build_list.get("P").put("id", replacement.getId());
                build_list.get("P").put("rating", npcService.swapPlayer(game, npc, replacement, "P", session, gameready, gameNpcRepository.getStat(npc, "batting"), false, false, gameready.getInningTop()));
                swapMessage = "Lineup Change: " + replacement.getFullName() + " for " + npc.getFullName();
            }
        }

        if (!hasReplacement) {
            npc = getAiPitcher(session, gameready);
            build_list.put("P", new LinkedHashMap<>());
            build_list.get("P").put("id", npc.getId());
            build_list.get("P").put("rating", gameNpcRepository.getStat(npc, "rating"));
        }


        int infield_rating = getBuildListRating(build_list, "P") + getBuildListRating(build_list, "C") + getBuildListRating(build_list, "1B") + getBuildListRating(build_list, "2B") + getBuildListRating(build_list, "3B") + getBuildListRating(build_list, "SS");
        int team_rating = infield_rating + getBuildListRating(build_list, "RF") + getBuildListRating(build_list, "LF") + getBuildListRating(build_list, "CF");
        TimerUtil ai482Timer = new TimerUtil("ai482Timer");
        GameNpc infield_npc = gameNpcRepository.findByPlayerAndLastName(npc.getPlayer(), "infield");
        

		GameNpc team_npc = gameNpcRepository.findByPlayerAndLastName(npc.getPlayer(), "team");
		ai482Timer.endTimer();
        GameNpcStat x = setFielding(infield_rating, infield_npc);
        entityManager.persist(x);

        if (team_rating >= 41) x = gameNpcRepository.setStat(team_npc, "rating", "1");
        else if (team_rating < 36) x = gameNpcRepository.setStat(team_npc, "rating", "3");
        else x = gameNpcRepository.setStat(team_npc, "rating", "2");

        entityManager.persist(x);

        if (!AutoplayUtil.isAutoplayGame((Integer) session.getAttribute(GAME))) {
        	gameStatusService.markStatusChanged(session, false);
        }
        
		entityManagerUtil.flush(session);

		return swapMessage;
	}

    public Map<String, Map<String, Object>> buildList(GameReady gameready, boolean inningTop) {
        Map<String, Map<String, Object>> buildList = new LinkedHashMap<>();
        for (String mappedPosition : NPC_MAPPED_POSITIONS.keySet()) {
            String positionLong = NPC_MAPPED_POSITIONS.get(mappedPosition);
            GameNpc npc = gameready.getNpcByPositionAndInningPart(positionLong, inningTop);
            buildList.put(mappedPosition, new LinkedHashMap<String, Object>() {{
                put("id", npc.getId());
                put("rating", gameNpcRepository.getStat(npc, "rating"));
            }});
        }
        return buildList;
    }

	public GameNpc getAiPitcher(HttpSession session, GameReady gameReady) {
		if (APBAHelper.isAutoplayGame(gameReady)) {
			if (gameReady.getInningTop()) {
				return gameReady.getHomePitcher();
			} else {
				return gameReady.getAwayPitcher();
			}
		} else {
			if (isPlayingHome(session, gameReady)) {
            	return gameReady.getHomePitcher();
        	} else {
            	return gameReady.getAwayPitcher();
        	}
		}
	}

	private static int getBuildListRating(Map<String, Map<String, Object>> build_list, String position) {
        if (!build_list.containsKey(position)) {
            return 0;
        }
        return PHPHelper.toInt((String) build_list.get(position).get("rating"));
    }

	public void forfeit(HttpSession session, Map<String, Object> game) {
        GameReady gameready = gameReadyRepository.findOneByGame((Integer) session.getAttribute(SessionKeys.GAME));

        endGameService.finishGame(gameready, Boolean.TRUE.equals(session.getAttribute(SessionKeys.AI)));
        entityManager.persist(gameready);

        game.put("game", gameready);

        gameConsoleUtil.writeToConsole(gameready, "Malcolm Simpleton Forfeits; Home Team Wins!");
    }
	
	public void newBatter(HttpSession session, Map<String, Object> game) {
		GameReady gameready = gameReadyRepository.findOneByGame((Integer) session.getAttribute(SessionKeys.GAME));
		GameNpc pitcher = getAiPitcher(session, gameready);
		boolean inningTop = gameready.getInningTop();
		LinkedHashMap<String, String> positions = new LinkedHashMap<String, String>() {{
			put("C", "catcher");
			put("1B", "first");
			put("2B", "second");
			put("3B", "third");
			put("SS", "shortstop");
			put("LF", "left");
			put("RF", "right");
			put("CF", "center");
		}};

		Map<String, Map<String, Object>> build_list = new LinkedHashMap<>();

        GameNpc npc = null;
        for (String p : positions.keySet()) {
            String f = positions.get(p);
            npc = gameready.getNpcByPositionAndInningPart(f, inningTop);
            build_list.put(p, new LinkedHashMap<>());
            build_list.get(p).put("id", npc.getId());
            build_list.get(p).put("rating", gameNpcRepository.getStat(npc, "rating"));
        }
        int runs = PHPHelper.toInt(avatarRepository.getRes(pitcher.getAvatar(), "initial_runs"));
        int er = PHPHelper.toInt(avatarRepository.getRes(pitcher.getAvatar(), "er"));
        boolean hasReplacement = false;
        
        if (PHPHelper.toInt(getAiRuns(session, gameready)) - runs >= 5 || er >= 5) {
            hasReplacement = true;
            GameNpc replacement = pitcherReplacement(session, game, pitcher);
            if (replacement.equals(pitcher) || replacement.isBenched()) {
                hasReplacement = false;
            }
            if (hasReplacement) {
                boolean playingHome;
                if(gameready.getInningTop()) {
                    playingHome = true;
                } else {
                    playingHome = false;
                }
                build_list.put("P", new LinkedHashMap<>());
                build_list.get("P").put("id", replacement.getId());
                build_list.get("P").put("rating", npcService.swapPlayer(game, pitcher, replacement, "P", session, gameready, gameNpcRepository.getStat(pitcher, "batting"), false, playingHome));
            }
        }

        if(!hasReplacement) {
            build_list.put("P", new LinkedHashMap<>());
            build_list.get("P").put("id", pitcher.getId());
            build_list.get("P").put("rating", gameNpcRepository.getStat(pitcher, "rating"));
        }

        /* Update the team rating */

        int infield_rating = getBuildListRating(build_list, "P") + getBuildListRating(build_list, "C") + getBuildListRating(build_list, "1B") + getBuildListRating(build_list, "2B") + getBuildListRating(build_list, "3B") + getBuildListRating(build_list, "SS");
        int team_rating = infield_rating + getBuildListRating(build_list, "RF") + getBuildListRating(build_list, "LF") + getBuildListRating(build_list, "CF");

        // TODO possible bug - just uses last npc from loop above - probably should be pitcher id
        GameNpc team_npc = gameNpcRepository.findByPlayerAndLastName(npc.getPlayer(), "team");
        GameNpc infield_npc = gameNpcRepository.findByPlayerAndLastName(npc.getPlayer(), "infield");

        setFielding(infield_rating, infield_npc);

        if (team_rating >= 41) gameNpcRepository.updateStat("1", team_npc, "rating");
        else if (team_rating < 36) gameNpcRepository.updateStat("3", team_npc, "rating");
        else gameNpcRepository.updateStat("2", team_npc, "rating");

        GameNpc runner3 = (GameNpc) game.get("runner3");

        if (gameready.getInning() >= 7 && runner3 != null && gameready.getCso() < 2 && gameready.getLead() <= 1) {
            gameready.setGameInfield("c");
            entityManager.persist(gameready);
        }

        if (!AutoplayUtil.isAutoplayGame((Integer) session.getAttribute(GAME))) {
        	gameStatusService.markStatusChanged(session, false);
        }
    }

    private Query query(HttpSession session, String qry, Stat combineStat) {
        return query(session, qry, "", combineStat);
    }

    // Conversion note: $x was never used
    /* XXX Not Touched */
    public Query query(HttpSession session, String qry, String p, Stat combineStat) {

        Query query = null;
        int playerId = 0;
        GameReady gameready = gameReadyRepository.findOneByGame((Integer) session.getAttribute(GAME));
        switch (qry) {
            case "replaceP":
                //Conversion note: have to use cross joins for not associated entities
                query = entityManager.createQuery("SELECT npc FROM GameNpc npc, AvatarStat avs, AvatarRes AS avr, AvatarStat AS avs_op " +
                        "JOIN avr.res AS r WITH r.reference = 'relieving_pitcher' " +
                        "WHERE avs.start LIKE '%cd_pos~P%' AND npc.player.id = :opponent " +
                        "AND avs_op.start LIKE '%sp_ip%' " +
                        "AND avs.avatar = npc.avatar " +
                        "AND avr.avatar = npc.avatar " +
                        "AND avs_op.stat.id = :stat " +
                        "AND avs.stat.id = :stat " +
                        "AND avs_op.avatar = npc.avatar " +
                        "ORDER BY avr.start DESC,avs_op.start DESC");
                break;

            case "pinch":
                //Conversion note: have to use cross joins for not associated entities
                query = entityManager.createQuery("SELECT npc FROM GameNpc npc, AvatarStat avs, AvatarStat AS avs2 " +
                        "WHERE avs.start NOT LIKE '%cd_epos~C%' AND npc.player.id = :opponent " +
                        "AND avs.start LIKE '%cd_epos%' AND avs2.start LIKE '%s_ops%' " +
                        "AND avs.avatar = npc.avatar " +
                        "AND avs.stat.id = :stat " +
                        "AND avs2.stat.id = :stat " +
                        "AND avs2.avatar = npc.avatar " +
                        "ORDER BY avs2.start DESC");
                break;
            case "injury1":
                query = entityManager.createQuery("SELECT npc FROM GameNpc npc, AvatarStat avs, AvatarStat AS avs2 " +
                        "WHERE avs.start LIKE :position " +
                        "AND npc.player.id = :opponent " +
                        "AND avs2.start LIKE '%s_ops%'" +
                        "AND avs.stat.id = :stat " +
                        "AND avs2.stat.id = :stat " +
                        "AND avs.avatar = npc.avatar " +
                        "AND avs2.avatar = npc.avatar " +
                        "ORDER BY avs2.start DESC");
                String pos = "%cd_epos~{\"" + p + "%";
                query.setParameter("position", pos);
                
                break;
            case "injury2":
            case "noRelievers":
                //Replacement should not be a pitcher
                query = entityManager.createQuery("SELECT npc FROM GameNpc npc, AvatarStat avs, AvatarStat AS avs2 " +
                        "WHERE avs.start NOT LIKE '%cd_epos~P%' AND npc.player.id = :opponent " +
                        "AND avs2.start LIKE '%s_ops%'" +
                        "AND avs.avatar = npc.avatar " +
                        "AND avs2.avatar = npc.avatar " +
                        "AND avs.stat.id = :stat " +
                        "AND avs2.stat.id = :stat " +
						"ORDER BY avs2.start DESC");
				break;
        }
        if(!APBAHelper.isAutoplayGame(gameready)) {
            if (session.getAttribute(SessionKeys.AI) != null && (Boolean) session.getAttribute(SessionKeys.AI)) {
                playerId = (int) session.getAttribute(OPPONENT);
            } else {
                playerId = (int) session.getAttribute(PLAYER);
            }
        } else {
            // Autoplay swaps session Ids during game play, this can cause queries to return the wrong team.
            // This else block resolves that issue by using a second set of IDs that never change.
            if ((int) session.getAttribute(HOME_AI_ID) == (int) session.getAttribute(PLAYER)) {
                playerId = (int) session.getAttribute(HOME_AI_ID);
            } else {
                playerId = (int) session.getAttribute(AWAY_AI_ID);
            }
        }
        assert query != null;
        query.setParameter("opponent", playerId);
        query.setParameter("stat", combineStat.getId());
        return query;
	}

    private Map<String, Map<String, Object>> buildPlayersMapWithRating(Collection<String> positions, GameReady gameready, boolean playerhome) {
        Map<String, Map<String, Object>> playersMap = new LinkedHashMap<>();
        for (String position : positions) {
            GameNpc npc = gameready.getNpcByPositionAndInningPart(GameService.translate(position), playerhome);

            if (npc != null) {
                playersMap.put(position, new LinkedHashMap<String, Object>() {{
                    put("id", npc.getId());
                    put("rating", gameNpcRepository.getStat(npc, "rating"));
                }});
            }
        }
        return playersMap;
    }

    public void injury(HttpSession session, Map<String, Object> game) {
        
        GameReady gameready = (GameReady) gameReadyRepository.findOneByGame((Integer) session.getAttribute(SessionKeys.GAME));
        Stat combineStat = StatRepositoryUtil.getStat("combine");
        List<String> positions = new ArrayList<>();
        positions.add("C");
        positions.add("1B");
        positions.add("2B");
        positions.add("3B");
        positions.add("SS");
        positions.add("LF");
        positions.add("CF");
        positions.add("RF");
        positions.add("DH");

        boolean playerHome = isPlayingHome(session, gameready);

        Map<String, Map<String, Object>> build_list = buildPlayersMapWithRating(positions, gameready, playerHome);

        for (String position : positions) {
            GameNpc npc = gameready.getNpcByPositionAndInningPart(GameService.translate(position), playerHome);
            if (npc == null) {
                continue;
            }

            if (npcService.isInjuredOrEjected(npc.getAvatar())) {
                //avatar_stat stores all outfielders as OF
                String queryParam = position;
                if ("LF".equals(position) || "CF".equals(position) || "RF".equals(position)) {
                    queryParam = "OF";
                }
                // TODO: Malcolm Injury replacement Problem?
                Query query = query(session, "injury1", queryParam, combineStat);

                List<GameNpc> relief_list = query.getResultList();
                List<GameNpc> reliefs = new ArrayList<GameNpc>();

                for (GameNpc n : relief_list) {
                    if (isNpcInjuredOrBenched(n)) {

                        reliefs.add(n);
                    }
                }
                if (reliefs.size() == 0) {
                    query = query(session, "injury2", combineStat);

                    relief_list = query.getResultList();
                    reliefs = new ArrayList<GameNpc>();

                    for (GameNpc n : relief_list) {
                        if (isNpcInjuredOrBenched(n)) {

                            reliefs.add(n);
                        }
                    }
                }

                if (reliefs.size() == 0) {
                    forfeit(session, game);
                    return;
                }
                GameNpc replacement = reliefs.get(0);
                build_list.get(position).put("id", replacement.getId());
                build_list.get(position).put("rating", npcService.swapPlayer(game, npc, replacement, position, session, gameready, gameNpcRepository.getStat(npc, "batting"), false, playerHome));
            }
        }

		/* Pitcher */

        final GameNpc npc = getAiPitcher(session, gameready);
        boolean hasReplacement = false;
        if (npcService.isInjuredOrEjected(npc.getAvatar())) {
            hasReplacement = true;
            GameNpc replacement = pitcherReplacement(session, game, npc);
            if (replacement.equals(npc) || !replacement.isBenched()) {
                hasReplacement = false;
            }
            if (hasReplacement) {
                build_list.put("P", new LinkedHashMap<String, Object>() {{
                    put("id", replacement.getId());
                    put("rating", npcService.swapPlayer(game, npc, replacement, "P", session, gameready, gameNpcRepository.getStat(npc, "batting"), false, isPlayingHome(session, gameready)));
                }});
            }
        }
        if (!hasReplacement) {
            //Conversion note: npc is still pitcher2
            build_list.put("P", new LinkedHashMap<String, Object>() {{
                put("id", npc.getId());
                put("rating", gameNpcRepository.getStat(npc, "rating"));
            }});
        }

		/* Update the team rating */
		int infield_rating = getBuildListRating(build_list, "P") + getBuildListRating(build_list, "C") + getBuildListRating(build_list, "1B") + getBuildListRating(build_list, "2B") + getBuildListRating(build_list, "3B") + getBuildListRating(build_list, "SS");
		int team_rating = infield_rating + getBuildListRating(build_list, "RF") + getBuildListRating(build_list, "LF") + getBuildListRating(build_list, "CF");

		GameNpc team_npc = gameNpcRepository.findByPlayerAndLastName(npc.getPlayer(), "team");
		GameNpc infield_npc = gameNpcRepository.findByPlayerAndLastName(npc.getPlayer(), "infield");

		GameNpcStat x = setFielding(infield_rating, infield_npc);
		entityManager.persist(x);

		if (team_rating >= 41) {
			x = gameNpcRepository.setStat(team_npc, "rating", "1");
		} else if (team_rating < 36) {
			x = gameNpcRepository.setStat(team_npc, "rating", "3");
		} else {
			x = gameNpcRepository.setStat(team_npc, "rating", "2");
		}

		entityManager.persist(x);

        if (!AutoplayUtil.isAutoplayGame((Integer) session.getAttribute(GAME))) {
        	gameStatusService.markStatusChanged(session, false);
        }
	}

	private GameNpcStat setFielding(int infieldRating, GameNpc infieldNpc) {

		int fielding = 1;

		if (infieldRating <= 35)
			fielding = 3;
		else if (infieldRating <= 40)
			fielding = 2;

		return gameNpcRepository.setStat(infieldNpc, "rating", Integer.toString(fielding));
	}

    private List<GameNpc> findReplacementPitcher(List<GameNpc> relief_list, List<GameNpc> reliefs) {
        List<GameNpc> tiedDefensiveRatingPitchers = new ArrayList<>();
        List<GameNpc> tiedHomeRunsAllowedRatings = new ArrayList<>();
        List<GameNpc> tiedPitcherControlRatings = new ArrayList<>();
        List<GameNpc> tiedWildPitchRatings = new ArrayList<>();
        List<GameNpc> tiedMoveToFirstRatings = new ArrayList<>();
        getTiedDefensiveRatingPitchers(relief_list, tiedDefensiveRatingPitchers);
        if (tiedDefensiveRatingPitchers.size() > 1) {
            getTiedHomeRunsAllowedRatings(tiedDefensiveRatingPitchers, tiedHomeRunsAllowedRatings);
        } else if (!tiedDefensiveRatingPitchers.isEmpty()) {
            reliefs = tiedDefensiveRatingPitchers;
        }
        if (tiedHomeRunsAllowedRatings.size() > 1) {
            tiedPitcherControlRatings = getTiedPitcherControlRatings(tiedHomeRunsAllowedRatings, tiedPitcherControlRatings);
        } else if (!tiedHomeRunsAllowedRatings.isEmpty()) {
            reliefs = tiedHomeRunsAllowedRatings;
        }
        if (tiedPitcherControlRatings.size() > 1) {
            getTiedWildPitchRaings(tiedPitcherControlRatings, tiedWildPitchRatings);
        } else if (!tiedPitcherControlRatings.isEmpty()) {
            reliefs = tiedPitcherControlRatings;
        }
        if (tiedWildPitchRatings.size() > 1) {
            getTiedMoveToFirstRatings(tiedWildPitchRatings, tiedMoveToFirstRatings);
        } else if (!tiedWildPitchRatings.isEmpty()) {
            reliefs = tiedWildPitchRatings;
        }
        reliefs = getBestCaseNpc(reliefs, tiedMoveToFirstRatings);
        return reliefs;
    }

    private List<GameNpc> getBestCaseNpc(List<GameNpc> reliefs, List<GameNpc> tiedMoveToFirstRatings) {
        GameNpc bestCase;
        if (tiedMoveToFirstRatings.size() > 1) {
            Random rand = new Random();
            bestCase = tiedMoveToFirstRatings.get(rand.nextInt(tiedMoveToFirstRatings.size()));
            reliefs.clear();
            reliefs.add(bestCase);
        } else if (!tiedMoveToFirstRatings.isEmpty()) {
            reliefs = tiedMoveToFirstRatings;
        }
        return reliefs;
    }

    private void getTiedMoveToFirstRatings(List<GameNpc> tiedWildPitchRatings, List<GameNpc> tiedMoveToFirstRatings) {
        int highestValue = 0;
        int value = 0;
        for (GameNpc loopNpc : tiedWildPitchRatings) {
            String res = avatarRepository.getCachedStat(loopNpc.getAvatar(), "cd_mf");
            if(res.equals("N/A")) res = "0";
            value = Integer.parseInt(res);
            if (value >= highestValue) {
                if (value > highestValue) tiedMoveToFirstRatings.clear();
                highestValue = value;
                tiedMoveToFirstRatings.add(loopNpc);
            }
        }
    }

    private void getTiedWildPitchRaings(List<GameNpc> tiedPitcherControlRatings, List<GameNpc> tiedWildPitchRatings) {
        int highestValue = 0;
        for (GameNpc loopNpc : tiedPitcherControlRatings) {
            int value = Integer.parseInt(avatarRepository.getCachedStat(loopNpc.getAvatar(), "cd_wp"));
            if (value >= highestValue) {
                if (value > highestValue) tiedWildPitchRatings.clear();
                highestValue = value;
                tiedWildPitchRatings.add(loopNpc);
            }
        }
    }

    private List<GameNpc> getTiedPitcherControlRatings(List<GameNpc> tiedHomeRunsAllowedRatings, List<GameNpc> tiedPitcherControlRatings) {
        ArrayList<GameNpc> temp = new ArrayList<>();
        for (GameNpc loopNpc : tiedHomeRunsAllowedRatings) {
            String zwValue = avatarRepository.getCachedStat(loopNpc.getAvatar(), "cd_control");
            if (zwValue.equals("Z")) { // W or Z, Z is preferred.
                tiedPitcherControlRatings.clear(); // Clear the tiedPitcherControlRatings to prevent bug.
                temp.add(loopNpc);
            } else if (temp.isEmpty()) {
                tiedPitcherControlRatings.add(loopNpc); // Only add if there isn't a preferred player.
            }
        }
        if (!temp.isEmpty()) {
            tiedPitcherControlRatings = temp;
        }
        return tiedPitcherControlRatings;
    }

    private void getTiedHomeRunsAllowedRatings(List<GameNpc> tiedDefensiveRatingPitchers, List<GameNpc> tiedHomeRunsAllowedRatings) {
        // Assigning value with letters for comparison.
	    HashMap<String, Integer> letterValues = new HashMap<>();
        letterValues.put("H", 4);
        letterValues.put("G", 3);
        letterValues.put("L", 2);
        letterValues.put("M", 1);
        letterValues.put("", 0);

        int highestValue = 0;
        for (GameNpc loopNpc : tiedDefensiveRatingPitchers) {
            String tempHa = avatarRepository.getCachedStat(loopNpc.getAvatar(), "cd_hrallowed");
            if (letterValues.get(tempHa) >= highestValue) {
                if (letterValues.get(tempHa) > highestValue) tiedHomeRunsAllowedRatings.clear();
                highestValue = letterValues.get(tempHa);
                tiedHomeRunsAllowedRatings.add(loopNpc);
            }
        }
    }

    private void getTiedDefensiveRatingPitchers(List<GameNpc> relief_list, List<GameNpc> tiedDefensiveRatingPitchers) {
        int defP = 0;
        for (GameNpc npc : relief_list) {
            if (!PHPHelper.isTrue(avatarRepository.getRes(npc.getAvatar(), "injury")) && !PHPHelper.isTrue(gameNpcRepository.getStat(npc, "position"))) {
                int tempPr = Integer.parseInt(avatarRepository.getCachedStat(npc.getAvatar(), "pr"));
                if (tempPr >= defP) {
                    defP = tempPr;
                    tiedDefensiveRatingPitchers.add(npc);
                }
            }
        }
    }

    public int getAiRuns(HttpSession session, GameReady gameReady) {
    	if (APBAHelper.isAutoplayGame(gameReady)) {
    		if (gameReady.getInningTop()) {
    			return gameReady.getAway();
    		} else {
    			return gameReady.getHome();
    		}
    	} else {
        	if ((Boolean) session.getAttribute("playerhome")) {
        		return gameReady.getHome();
        	} else {
        		return gameReady.getAway();
        	}
    	}
    }
}
