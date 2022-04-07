package com.lixar.apba.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lixar.apba.domain.GameStatus;

public interface GameStatusRepository extends JpaRepository<GameStatus, Integer> {
	GameStatus findOneByGame(Integer gameId);
}
