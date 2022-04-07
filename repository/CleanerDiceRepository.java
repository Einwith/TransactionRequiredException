package com.lixar.apba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lixar.apba.domain.CleanerDice;

public interface CleanerDiceRepository extends JpaRepository<CleanerDice, Integer> {

	CleanerDice findOneByGid(int gid);
	
	default void removeDicesByGame(int gid) {
		CleanerDice cleanerDice = findOneByGid(gid);
		if (cleanerDice != null) {
			removeDice(cleanerDice.getDiceIdsArray());
			removeDiceResults(cleanerDice.getDiceIdsArray());
			delete(cleanerDice);
		}
	}
	
	@Modifying
	@Query("DELETE FROM Dice dc WHERE dc.id IN (:ids)")
	public void removeDice(@Param("ids") int[] ids);
	
	@Modifying
	@Query("DELETE FROM DiceResult dr WHERE dr.dice IN (:ids)")
	public void removeDiceResults(@Param("ids") int[] ids);
}
