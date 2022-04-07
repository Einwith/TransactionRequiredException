package com.lixar.apba.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lixar.apba.domain.Avatar;
import com.lixar.apba.domain.Game;
import com.lixar.apba.domain.GameSubstitution;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface GameSubstitutionRepository extends JpaRepository<GameSubstitution, Integer> {

	List<GameSubstitution> findAllByGameAndPositionAndInning(Game game, String position, Integer inning);

	List<GameSubstitution> findAllByGameIdAndPlayerIdAndInningAndIsSubstitutedAndPinchNotNull(Integer gameId, Integer playerId, Integer inning, Boolean isSubstituted);

	default List<Map<String, Object>> subsByPinch(Integer gameId, Integer playerId, Integer inning) {
		List<GameSubstitution> records = findAllByGameIdAndPlayerIdAndInningAndIsSubstitutedAndPinchNotNull(gameId, playerId, inning, false);
		List<Map<String, Object>> result = new ArrayList<>();
		for (GameSubstitution sub : records) {
			result.add(new LinkedHashMap<String, Object>() {{
				put("pos", sub.getPosition());
				put("pinch", sub.getPinch());
				put("to", sub.getAvatarTo().getId());
			}});
		}
		return result;
	}

	Long countByGameIdAndPlayerIdAndInningAndIsSubstitutedAndPinchNotNull(Integer gameId, Integer playerId, Integer inning, Boolean isSubstituted);

	default Long countSubstitutions(Integer gameId, Integer playerId, Integer inning) {
		return countByGameIdAndPlayerIdAndInningAndIsSubstitutedAndPinchNotNull(gameId, playerId, inning, false);
	}

	@Modifying
	@Transactional
	void deleteByGame(Game game);

	Long countByAvatarToAndPinchNotNull(Avatar avatarTo);

	Long countByAvatarFromAndAvatarToAndPinchIsNull(Avatar avatarFrom, Avatar avatarTo);

	default boolean isAvatarPinchHitterOrRunner(Avatar avatar) {
		Long pinchSubstitutionCount = countByAvatarToAndPinchNotNull(avatar);
		if (pinchSubstitutionCount == 0) {
			return false;
		} else {
			return countByAvatarFromAndAvatarToAndPinchIsNull(avatar, avatar) == 0;
		}
	}

	GameSubstitution findFirstByAvatarToAndGameId(Avatar avatarTo, Integer gameId);
}
