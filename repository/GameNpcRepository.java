package com.lixar.apba.repository;

import static com.lixar.apba.core.util.GameConstants.TEAM_LABEL;
import static com.lixar.apba.core.util.GameConstants.INFIELD_LABEL;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lixar.apba.domain.Dice;
import com.lixar.apba.domain.GameNpc;
import com.lixar.apba.domain.GameNpcStat;
import com.lixar.apba.domain.GamePlayer;
import com.lixar.apba.domain.GameSector;
import com.lixar.apba.service.util.TimerUtil;

public interface GameNpcRepository extends JpaRepository<GameNpc, Integer> {

	final Logger log = LoggerFactory.getLogger(GameNpcRepository.class);

	List<String> SETUP_POSITIONS = new ArrayList<String>() {{
		add("ondeck");
		add("batter");
		add("pitcher");
		add("catcher");
		add("first");
		add("second");
		add("third");
		add("shortstop");
		add("left");
		add("center");
		add("right");
		add("runner1");
		add("runner2");
		add("runner3");
	}};

	@Query("SELECT avs FROM GameNpcStat avs, Stat s WHERE avs.npc = :npc AND avs.stat = s AND s.reference = :stat")
	List<GameNpcStat> _loadStat(@Param("npc") GameNpc npc, @Param("stat") String stat);

	default GameNpcStat loadStat(GameNpc npc, String stat) {
		List<GameNpcStat> result = _loadStat(npc, stat);
		if ((result.size() > 0) && (result.get(0) != null)) {
			return result.get(0);
		}
		return new GameNpcStat();
	}

	@Query("SELECT gn FROM GameNpc gn WHERE gn.player.id = :id")
	List<GameNpc> findByPlayerId(@Param("id") Integer id);

	@Query("SELECT avs.start FROM GameNpcStat avs, Stat s WHERE avs.npc = :npc AND avs.stat = s AND s.reference = :stat")
	String _getStat(@Param("npc") GameNpc npc, @Param("stat") String stat);

	default String getStat(GameNpc npc, String stat) {
		try {
			String result = _getStat(npc, stat);
			return result;
		} catch (Exception e) {
			return "0";
		}
		
		
		
	}

	GameNpc findByPlayerAndLastName(GamePlayer player, String lastName);

	GameNpc findByPlayerIdAndLastName(Integer playerId, String lastName);

	GameNpc findFirstBySector(GameSector sector);
	
	//select d from Dice d where d.client.id = :client AND d.id = :id
	
	
	

	default GameNpc findOneBySectorSafe(GameSector sector) {
		
		try {
			TimerUtil gamenpcqueryTimer = new TimerUtil("gamenpcqueryTimer");
			GameNpc result = findFirstBySector(sector);
			gamenpcqueryTimer.endTimer();
			return result;
		} catch (Exception e) {
			return null;
		}
		

					
		
		
		
	}

	GameNpc findOneById(Integer id);

    @Query("SELECT npc FROM ExternalGame eg, GameNpc npc, GameNpcStat avs, Stat s WHERE eg.intGid = :game AND npc.player = eg.externalHomePlayerId AND avs.npc.id = npc.id AND s.reference = :sn AND avs.stat.id = s.id order by avs.start")
    List<GameNpc> searchByTeamBattingHomeTeam(@Param("game") Integer gameId, @Param("sn") String reference);

	@Query("SELECT npc FROM ExternalGame eg, GameNpc npc, GameNpcStat avs, Stat s WHERE eg.intGid = :game AND npc.player = eg.externalAwayPlayerId AND avs.npc.id = npc.id AND s.reference = :sn AND avs.stat.id = s.id order by avs.start")
	List<GameNpc> searchByTeamBattingAwayTeam(@Param("game") Integer gameId, @Param("sn") String reference);

	default List<GameNpc> searchByTeamBatting(Integer gameId) {
		return searchByTeamBatting(gameId, true);
	}

	default List<GameNpc> searchByTeamBatting(Integer gameId, boolean homeTeam) {
		String reference = "batting";
		if (homeTeam) {
			return searchByTeamBattingHomeTeam(gameId, reference);
		} else {
			return searchByTeamBattingAwayTeam(gameId, reference);
		}
	}

	default GameNpcStat setStat(GameNpc npc, String res, String value) {
		GameNpcStat result = loadStat(npc, res);
		result.setStart(value);
		return result;
	}


    @Modifying
    @Query(value = "update game_npc_stat gnpcs set gnpcs.start = ?1 where gnpcs.npc = ?2 and gnpcs.stat = (select id from stat where reference = ?3)", nativeQuery = true)
    void updateStatQuery(String value, Integer npcId, String res);

    default void updateStat(String value, GameNpc npc, String res) {
        if ("rating".equals(res)) {
            npc.setRatingStat(value);
        }
        updateStatQuery(value, npc.getId(), res);
    }

	@Query("SELECT npc FROM GameNpc npc, GameNpcStat avs, Stat s WHERE npc.player.id = :pid AND avs.npc.id = npc.id AND s.reference = :sn AND avs.stat.id = s.id order by avs.start")
	List<GameNpc> _searchByPlayerBatting(@Param("pid") Integer playerId, @Param("sn") String reference);

	default List<GameNpc> searchByPlayerBatting(Integer playerId) {
		return _searchByPlayerBatting(playerId, "batting");
	}

	@Query("SELECT count(gn) from GameNpc gn where gn.sector.cluster.game.id = :gameId and gn.sector.name in ('batter', 'runner1', 'runner2', 'runner3', 'ondeck')")
	int countPlayersOnBasePlateAndDeck(@Param("gameId") Integer gameId);

	@Query("SELECT gn FROM GameNpc gn WHERE gn.sector.cluster.game.id = :id")
	List<GameNpc> findByGameId(@Param("id") Integer id);
	
	default List<GameNpc> getAllNpcsByGame(Integer gameId, String dugout) {
		List<GameNpc> npcs = findByGameId(gameId);
		List<GameNpc> removeNpcs = new ArrayList<GameNpc>();
		for (GameNpc npc : npcs) {
			String npcSector = npc.getSector().getName();
			if (!SETUP_POSITIONS.contains(npcSector) && 
					(!npcSector.equals(dugout) || 
					(!npc.getLastName().equals(TEAM_LABEL) && !npc.getLastName().equals(INFIELD_LABEL)) ) ) {
				
				removeNpcs.add(npc);
			}
		}
		
		removeNpcs.forEach( (npc) -> { npcs.remove(npc); } );
		
		return npcs;
	}
	
}
