package com.lixar.apba.repository;

import com.lixar.apba.domain.DiceResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface DiceResultRepository extends JpaRepository<DiceResult, Integer> {

    List<DiceResult> findAllByDicePoolAndDiceSidAndRequirement(int dicePool, int diceSid, String requirement);
    
    List<DiceResult> findAllByDiceAndResultText(int dice, String diceRoll);
    
    List<DiceResult> findAllByDiceInAndResultTextIn(List<Integer> diceList, String[] diceRolls);

    @Query("select d from DiceResult d where d.dicePool < 100 order by d.dicePool, d.diceSid, d.requirement")
    List<DiceResult> findAllActiveOrdered();

    @Query("select d from DiceResult d where d.dicePool < 100 order by d.id")
    List<DiceResult> findAllActiveOrderByIdAsc();
    
    List<DiceResult> findByDice(Integer dice);
    
    DiceResult findOneByDicePoolAndDiceSidAndRequirement(int dicePool, int diceSid, String requirement);

	List<DiceResult> findAllByDiceAndDicePoolAndDiceSid(Integer id, int pool, int sid);

	List<DiceResult> findAllByDicePoolAndDiceSid(int pool, int sid);
}
