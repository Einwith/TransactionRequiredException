package com.lixar.apba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lixar.apba.domain.GamePlayer;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Integer>{
	@Query("SELECT p.nickname FROM GamePlayer p WHERE p.id = :id")
	public String findNicknameById(@Param("id") Integer id);
}
