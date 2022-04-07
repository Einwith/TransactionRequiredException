package com.lixar.apba.repository.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lixar.apba.domain.Stat;

public class StatRepositoryUtil {

	private static boolean allStats = false;
	private static Map<String, Stat> cachedStats = new HashMap<>();
	
	/**
	 * Gets the Stat via specified name if it is cached, null otherwise.
	 * @param name
	 * @return
	 */
	public static Stat getStat(String name) {
		if (cachedStats.containsKey(name)) {
			return cachedStats.get(name);
		} else {
			return null;
		}
	}
	
	/**
	 * Adds the Stat to the cache.
	 * @param stat
	 */
	public static void addStatToCache(Stat stat) {
		cachedStats.put(stat.getReference(), stat);
	}
	
	/**
	 * Retrieves all Stats from the cache as a List
	 * @return
	 */
	public static List<Stat> getAllStats() {
		if (allStats) {
			return new ArrayList<Stat>(cachedStats.values());
		}
		return null;
	}

	/* 
	 * Adds all Stats from DB to the cache
	 * @param statList
	 */
	public static void addAllStats(List<Stat> statList) {
		for (Stat stat : statList) {
			addStatToCache(stat);
		}
		allStats = true;
	}
}