package com.lixar.apba.repository;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lixar.apba.domain.Dice;
import com.lixar.apba.domain.GameNpc;
import com.lixar.apba.service.util.TimerUtil;

public interface DiceRepository extends JpaRepository<Dice, Integer> {
	
	final Logger log = LoggerFactory.getLogger(DiceRepository.class);
	
	Dice findOneById(Integer id);
	
	
	@Query("select d from Dice d where d.client.id = :client AND d.pool = :pool AND d.sid = :sid")
	Dice findFirstByClientAndPoolAndSid(@Param("client") Integer client, @Param("pool") Integer pool, @Param("sid") Integer sid);
	
	
	default Dice findOneByClientAndPoolAndSidSafe(@Param("client") Integer client, @Param("pool") Integer pool, @Param("sid") Integer sid) {
		try {
			
		TimerUtil dicequeryTimer = new TimerUtil("Derived dicequeryTimer");
		Dice result = findFirstByClientAndPoolAndSid(client, pool, sid);
		dicequeryTimer.endTimer();
		
		
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}
