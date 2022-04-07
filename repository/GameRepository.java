package com.lixar.apba.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lixar.apba.domain.Game;
import com.lixar.apba.domain.GameStat;

public interface GameRepository extends JpaRepository<Game, Integer> {

	@Query("SELECT avs.start FROM GameStat avs, Stat s WHERE avs.game.id = :id AND avs.stat = s AND s.reference = :stat")
	List<String> _getStat(@Param("id") Integer gameId, @Param("stat") String stat);

	default String getStat(Integer ganeId, String stat) {
		// Note: this differs from the other stats in that it can return null
		List<String> result = _getStat(ganeId, stat);
		if ((result.size() > 0) && (result.get(0) != null)) {
			return result.get(0);
		}
		return null;
	}

	@Query("SELECT avs FROM GameStat avs, Stat s WHERE avs.game = :id AND avs.stat = s AND s.reference = :stat")
	List<GameStat> _loadStat(@Param("id") Game game, @Param("stat") String stat);

	default GameStat loadStat(Game game, String stat) {
		List<GameStat> result = _loadStat(game, stat);
		if ((result.size() > 0) && (result.get(0) != null)) {
			return result.get(0);
		}
		return new GameStat();
	}

	@Query("SELECT avs FROM GameStat avs, Stat s WHERE avs.game.id = :id AND avs.stat = s AND s.reference = :stat")
	List<GameStat> _loadStat(@Param("id") Integer gameId, @Param("stat") String stat);

	default GameStat loadStat(Integer gameId, String stat) {
		List<GameStat> result = _loadStat(gameId, stat);
		if ((result.size() > 0) && (result.get(0) != null)) {
			return result.get(0);
		}
		return new GameStat();
	}
	@Query("SELECT gm FROM Game gm, ExternalGame eg, GameReady gr WHERE gm.id = eg.intGid AND " +
        "gm.id = gr.game.id AND (eg.externalHomePlayer <> eg.externalAwayPlayer OR gr.gameActive = false) AND eg.tournamentId is null AND gm.startDate < :date")
	List<Game> findByStartDateLessThan(@Param("date") Integer date, Pageable limit);

	@Query("SELECT gm FROM Game gm, ExternalGame eg WHERE gm.id = eg.intGid AND eg.tournamentId = :tournamentId")
	List<Game> findAllTournamentGames(@Param("tournamentId") Long tournamentId);
}
