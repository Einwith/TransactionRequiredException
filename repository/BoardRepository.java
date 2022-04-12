package com.lixar.apba.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lixar.apba.domain.Board;

public interface BoardRepository extends JpaRepository<Board, Integer> {

	Board findOneByName(String name);
}