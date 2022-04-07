package com.lixar.apba.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lixar.apba.domain.ExternalGame;

public interface ExternalGameRepository extends JpaRepository<ExternalGame, Integer> {
	ExternalGame findOneByExtGid(String extGid);

	ExternalGame findOneByIntGid(Integer intGid);
	
	ExternalGame findOneByHomeGUID(UUID guid);
	
	@Query("select eg from ExternalGame eg where eg.homeGUID = :guid OR eg.awayGUID = :guid ")
	ExternalGame findOneByGUID(@Param("guid") UUID guid);
	
	@Query("select eg from ExternalGame eg where eg.externalHomePlayer = eg.externalAwayPlayer AND eg.extGid = :extGid ")
	ExternalGame findOneSolitaireByExtGid(@Param("extGid") String extGid);
	
	@Query("select eg from ExternalGame eg where eg.extGid = :extGid ")
	ExternalGame findGameByExtGid(@Param("extGid") String extGid);
}
