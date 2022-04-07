package com.lixar.apba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lixar.apba.domain.GameLineup;

public interface GameLineupRepository extends JpaRepository<GameLineup, Integer> {

	@Query("SELECT gl FROM GameLineup gl WHERE gl.game.id = :gameId AND gl.player.id = :playerId")
	GameLineup findOneByGameAndPlayer(@Param("gameId") Integer gameId, @Param("playerId") Integer playerId);
}
