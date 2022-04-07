package com.lixar.apba.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lixar.apba.domain.GameSector;

public interface GameSectorRepository extends JpaRepository<GameSector, Integer> {

	@Query("SELECT s FROM GameSector s, GameCluster gc WHERE gc.game.id = :gid AND gc.sid = :cid AND s.cluster = gc AND s.name = :name")
	GameSector findByName(@Param("gid") Integer gid, @Param("cid") Integer cid, @Param("name") String name);

	@Query("SELECT s FROM GameSector s, GameCluster gc WHERE gc.game.id = :gid AND gc.sid = :cid AND s.cluster = gc")
	List<GameSector> findAllByGame(@Param("gid") Integer gid,  @Param("cid") Integer cid);

	@Query("SELECT s FROM GameSector s, GameCluster gc WHERE gc.game.id = :gid AND gc.sid = :cid AND s.cluster = gc AND s.name IN ('runner1', 'runner2', 'runner3', 'runner4')")
	List<GameSector> findRunnersByGame(@Param("gid") Integer gid, @Param("cid") Integer cid, Sort sort);
}
