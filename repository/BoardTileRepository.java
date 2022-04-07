package com.lixar.apba.repository;

import java.util.List;

import com.lixar.apba.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lixar.apba.domain.BoardTile;

public interface BoardTileRepository extends JpaRepository<BoardTile, Integer>{

	List<BoardTile> findByBoard(Board boardId);
}
