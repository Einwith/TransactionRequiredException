package com.lixar.apba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lixar.apba.domain.Game;
import com.lixar.apba.domain.GameEvent;

public interface GameEventRepository extends JpaRepository<GameEvent, Integer> {
	
	public List<GameEvent> findAllByGameAndHomeTrue(Game game);
	
	public List<GameEvent> findAllByGameAndAwayTrue(Game game);
}
