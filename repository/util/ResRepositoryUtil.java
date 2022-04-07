package com.lixar.apba.repository.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lixar.apba.domain.Res;

public class ResRepositoryUtil {

	private static boolean allResources = false;
	private static Map<String, Res> cachedResources = new HashMap<>();
	
	/**
	 * Gets the Resource via specified name if it is cached, null otherwise.
	 * @param name
	 * @return
	 */
	public static Res getRes(String name) {
		if (cachedResources.containsKey(name)) {
			return cachedResources.get(name);
		} else {
			return null;
		}
	}
	
	/**
	 * Adds the Resource to the cache.
	 * @param res
	 */
	public static void addResToCache(Res res) {
		cachedResources.put(res.getReference(), res);
	}
	
	/**
	 * Retrieves all Resources from the cache as a List
	 * @return
	 */
	public static List<Res> getAllResources() {
		if (allResources) {
			return new ArrayList<Res>(cachedResources.values());
		}
		return null;
	}

	/**
	 * Add all Resources from DB to the cache
	 * @param resList
	 */
	public static void addAllRes(List<Res> resList) {
		for (Res res : resList) {
			addResToCache(res);
		}
		allResources = true;
	}
}
