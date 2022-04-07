package com.lixar.apba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lixar.apba.domain.APBASession;
import com.lixar.apba.domain.Game;
import com.lixar.apba.domain.GamePlayer;

public interface ApbaSessionRepository extends JpaRepository<APBASession, Integer> {

	APBASession findOneByGameAndPlayer(Game game, GamePlayer player);

	@Query("SELECT a FROM APBASession a WHERE a.game = :game AND a.player.id = :playerId")
	APBASession findOneByGameAndPlayer(@Param("game") Game game, @Param("playerId") Integer playerId);

	@Query("SELECT a FROM APBASession a WHERE a.game.id = :gameId AND a.player.id = :playerId")
	APBASession findOneByGameAndPlayer(@Param("gameId") Integer gameId, @Param("playerId") Integer playerId);
}
