package com.lixar.apba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lixar.apba.domain.Game;
import com.lixar.apba.domain.GameReady;

public interface GameReadyRepository extends JpaRepository<GameReady, Integer> {

	GameReady findOneByGame(Game game);
	
	GameReady findOneByGameAndGameActive(Game game, boolean gameActive);

	@Query("SELECT gr FROM GameReady gr WHERE gr.game.id = :gameId")
	GameReady findOneByGame(@Param("gameId") Integer gameId);

	@Query("SELECT gr FROM GameReady gr JOIN FETCH gr.homePlayer JOIN FETCH gr.awayPlayer "
			+ "JOIN FETCH gr.homePitcher p1 JOIN FETCH p1.avatar "
			+ "JOIN FETCH gr.awayPitcher p2 JOIN FETCH p2.avatar "
			+ "WHERE gr.game.id = :gameId")
	GameReady findOneByGameAndFetchPlayers(@Param("gameId") Integer gameId);
}
