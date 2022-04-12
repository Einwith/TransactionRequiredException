package com.lixar.apba.core.util;

import static com.lixar.apba.core.util.GameConstants.*;
import static com.lixar.apba.web.ModelConstants.HIGH_PRECISION_CONTEXT;
import static com.lixar.apba.web.SessionKeys.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.lixar.apba.domain.GameNpc;
import com.lixar.apba.domain.GameReady;
import org.apache.commons.lang3.StringUtils;

import com.lixar.apba.domain.GameStat;
import com.lixar.apba.repository.GameRepository;
import com.lixar.apba.web.ModelConstants.Side;

import javax.servlet.http.HttpSession;

public class APBAHelper {

	private static final int IP_SCALE = 4;
	private static final String TOKEN_PREFIX = "token";
	private static final String INFINITY = "Inf";

	public static void filterStats(Map<String, Object> stats, List<String> majorIndexes, List<String> minorIndexes, List<String> except) {
		if ((majorIndexes != null) && (!majorIndexes.isEmpty())) {
			for (String m_idx : majorIndexes) {
				Object value = stats.get(m_idx);
				if (value instanceof Map) {
					if (((Map<String, Object>) value).isEmpty()) {
						continue;
					}
					filterStats((Map<String, Object>) value, Collections.emptyList(), minorIndexes, except);
				}
			}
		} else {
			if ((minorIndexes == null) || (minorIndexes.isEmpty())) {
				minorIndexes = new ArrayList<String>(stats.keySet());
			}
			if(except == null) {
				except = Collections.emptyList();
			}
			for (String s_idx : minorIndexes) {
				if (stats.containsKey(s_idx) && !except.contains(s_idx)) {
					Object val = stats.get(s_idx);
					if (val instanceof String) {
						String str = (String) val;
						if (str.contains(PERIOD)) {
							stats.put(s_idx, StringUtils.stripStart(str, INT_STR_ZERO));
						}
					}
				}
			}
		}
	}

	public static Object calculateERA(BigDecimal earnedRuns, BigDecimal outs) {
		boolean isOutsZero = outs.compareTo(BigDecimal.ZERO) == 0;
		boolean isEarnedRunsZero = earnedRuns.compareTo(BigDecimal.ZERO) == 0;
		if (isOutsZero && isEarnedRunsZero) {
			return EMPTY_SPACE;
		} else if (isOutsZero) {
			return INFINITY;
		} else if (isEarnedRunsZero) {
			return INT_STR_ZERO;
		} else {
			BigDecimal inningsPitched = outs.divide(new BigDecimal(3), IP_SCALE, RoundingMode.DOWN);
			return PHPHelper.number_format(new BigDecimal(9).multiply(earnedRuns).divide(inningsPitched, HIGH_PRECISION_CONTEXT), 2);
		}
	}

	public static boolean shouldSelectAILineup(HttpSession session, GameRepository gameRepository) {
		Integer gameId = (Integer) session.getAttribute(GAME);
		GameStat statMicroManagerLineup = gameRepository.loadStat(gameId, GameConstants.RULE_ID_MICRO_MANAGER_LINEUP);
		 return statMicroManagerLineup != null && PHPHelper.toInt(statMicroManagerLineup.getStart()) > 0;
	}
	
	public static boolean isSelectingAway(HttpSession session) {
		return Boolean.TRUE.equals(session.getAttribute(SELECTING_AWAY));
    }

    public static String externalGameIdToToken(String gameId) {
        return TOKEN_PREFIX + gameId;
    }
    
    public static boolean isPlayerVsPlayerGame(HttpSession session) {
    	return !isSolitaireGame(session) && !isAutoplayGame(session);
    }
    
    public static boolean isPlayerVsAiGame(HttpSession session) {
    	if (session.getAttribute(AI) == null) {
    		return false;
    	}
    	return (boolean) session.getAttribute(AI);
    }

	public static boolean isSolitaireGame(HttpSession session) {
		return Objects.equals(session.getAttribute(OPPONENT_ID), session.getAttribute(PLAYER_ID));
	}

    public static boolean isAutoplayGame(GameReady gameReady) {
	    return gameReady.getHomeMicroManagerId() != null && gameReady.getAwayMicroManagerId() != null;
    }
    
    public static boolean isAutoplayGame(HttpSession session) {
    	return (boolean) session.getAttribute(AUTOPLAY_GAME);
    }

    public static Integer getMicroManagerId(GameReady gameReady, HttpSession session) {
    	if (isAutoplayGame(gameReady)) {
    		return gameReady.getMicroManagerId(gameReady.getInningTop() ? Side.HOME : Side.AWAY);
    	} else {
		    return gameReady.getMicroManagerId(!isPlayingHome(session, gameReady) ? Side.HOME : Side.AWAY);
    	}
    }

    public static boolean isPlayerHome(HttpSession session) {
		return  Boolean.TRUE.equals(session.getAttribute(PLAYER_HOME));
	}
    
    public static boolean isPlayingHome(HttpSession session, GameReady gameReady) {
        return session.getAttribute(PLAYER).equals(gameReady.getHomePlayer().getId());
    }
    
    public static boolean isPlayingHome(GameReady gameready, GameNpc npc) {
    	return gameready.getHomePlayer().getId().equals(npc.getPlayer().getId());
    }
    
    public static boolean isAiPlayingHome(HttpSession session, GameReady gameReady) {
    	if (isAutoplayGame(gameReady)) {
			return gameReady.getHomePlayer().getId().equals((int) session.getAttribute(HOME_AI_ID));
    	}
        return session.getAttribute(OPPONENT).equals(gameReady.getHomePlayer().getId());
    }
    
    public static void alternateSelectingAway(HttpSession session) {
    	session.setAttribute(SELECTING_AWAY, !((boolean) session.getAttribute(SELECTING_AWAY)));
    }
    
    public static String getOrdinalSuffix(int number) {
    	if (number < 11 || number > 13) {
            switch (number % 10) {
                case 1:
                    return number + ORDINAL_FIRST;
                case 2:
                	return number + ORDINAL_SECOND;
                case 3:
                	return number + ORDINAL_THIRD;
                default:
                	return number + ORDINAL_GENERAL;
            }
        } else {
        	return number + ORDINAL_GENERAL;
        }
    }
}
