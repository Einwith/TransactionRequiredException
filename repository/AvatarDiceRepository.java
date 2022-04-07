package com.lixar.apba.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lixar.apba.domain.Avatar;
import com.lixar.apba.domain.AvatarDice;

public interface AvatarDiceRepository extends JpaRepository<AvatarDice, Integer> {

	AvatarDice findByAvatarAndPriority(Avatar avatar, int priority);
	
	List<AvatarDice> findAllByAvatar(Avatar avatar);
}
