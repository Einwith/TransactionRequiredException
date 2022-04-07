package com.lixar.apba.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lixar.apba.domain.GameConsole;

public interface GameConsoleRepository extends JpaRepository<GameConsole, Integer> {

	// Conversion note: simplified to return entire console instead of just the three fields
	@Query("SELECT gc FROM GameConsole gc WHERE gc.game.id = :id AND gc.level <= :level order by gc.id desc")
	public List<GameConsole> _getMessages(@Param("id") Integer id, @Param("level") Integer level, Pageable pageable);
	
	default List<GameConsole> getMessages(Integer id) {
		return getMessages(id, 3);
	}

	default List<GameConsole> getMessages(Integer id, Integer level) {
		return _getMessages(id, level, PageRequest.of(0, 10));
	}
	
	@Query("SELECT gc FROM GameConsole gc WHERE gc.game.id = :id AND gc.level <= :level order by gc.id desc")
	public List<GameConsole> getAllMessages(@Param("id") Integer id, @Param("level") Integer level);
	
	default List<GameConsole> getAllMessages(Integer id) {
		return getAllMessages(id, 4);
	}
}
