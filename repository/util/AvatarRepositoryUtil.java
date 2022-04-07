package com.lixar.apba.repository.util;

import java.util.HashMap;
import java.util.Map;

import com.lixar.apba.domain.Avatar;
import com.lixar.apba.domain.AvatarRes;
import com.lixar.apba.domain.AvatarStat;

/**
 * A class to cache Avatar objects for use in autoplay games.
 *
 */
public class AvatarRepositoryUtil {
	
	
	private static Map<Integer, Map<Integer, Avatar>> autoplayAvatarCache = new HashMap<>();
	private static Map<Integer, Map<Integer, Map<String, AvatarStat>>> autoplayAvatarStatMap = new HashMap<>();
	private static Map<Integer, Map<Avatar, Map<String, AvatarRes>>> autoplayAvatarResMap = new HashMap<>();
	
	
	
	/**
	 * Checks if the Map of cached Avatars contains the Avatar associated with the input keys.
	 * @param gameId - Retrieved from Game object (which can be found in GameReady as well).<br>Used to distinguish sessions/games.
	 * @param avatarId - The desired avatar denoted by id.
	 * @return Avatar from cache, or null.
	 */
	public static Avatar getAvatar(Integer gameId, Integer avatarId) {
		if (autoplayAvatarCache.containsKey(gameId) && autoplayAvatarCache.get(gameId).containsKey(avatarId)) {
			return autoplayAvatarCache.get(gameId).get(avatarId);
		}
		return null;
	}
	
	/**
	 * Adds the specified Avatar object to the autoplay cache with key of gameId
	 * @param gameId - Retrieved from Game object (which can be found in GameReady as well).<br>Used to distinguish sessions/games.
	 * @param avatar - The avatar to be cached.
	 */
	public static void addAvatarToCache(Integer gameId, Avatar avatar) {
		Map<Integer, Avatar> avatars;
		
		if (autoplayAvatarCache.containsKey(gameId)) {
			avatars = autoplayAvatarCache.get(gameId);
		} else {
			avatars = new HashMap<>();
		}
		
		avatars.put(avatar.getId(), avatar);
		
		autoplayAvatarCache.put(gameId, avatars);
	}
}
