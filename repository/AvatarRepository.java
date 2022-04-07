package com.lixar.apba.repository;

import static com.lixar.apba.core.util.GameConstants.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.lixar.apba.core.util.PHPHelper;
import com.lixar.apba.domain.Avatar;
import com.lixar.apba.domain.AvatarRes;
import com.lixar.apba.domain.AvatarStat;
import com.lixar.apba.web.AvatarCache;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface AvatarRepository extends JpaRepository<Avatar, Integer> {

	static final String MISSING_STAT = "-777";
	static final AvatarCache avatarStatCache = new AvatarCache();
	static final AvatarCache avatarResCache = new AvatarCache();
	static final String NO_SEASON_STAT = "-";
	static final Set<String> SEASON_STATS = new HashSet<>(Arrays.asList("sp_g", "sp_gs", "sp_w", "sp_l", "sp_sv",
			"sp_era", "sp_bb", "sp_so", "sp_hr", "s_ba", "s_slg", "s_obp", "s_g", "s_ab", "s_r", "s_h", "s_hr", "s_rbi",
			"s_sb", "s_ops", "sp_ip", "sb_g", "sb_pa", "sb_ab", "sb_h", "sb_2b", "sb_3b", "sb_hr", "sb_tb", "sb_r",
			"sb_rbi", "sb_so", "sb_bb", "sb_ibb", "sb_hbp", "sb_sh", "sb_sf", "sb_gdp", "sb_avg", "sb_obp", "sb_slg",
			"sb_ops", "sb_iso", "sb_seca", "sb_ta", "sb_rc", "sb_rcg", "sb_asb", "sb_cs", "sb_sbp", "sb_sb", "sd_g",
			"sd_tc", "sd_sc", "sd_po", "sd_a", "sd_e", "sd_dp", "sd_tp", "sd_fld", "sd_tcg", "sd_pb", "ttl_g", "ttl_tc",
			"ttl_sc", "ttl_po", "ttl_a", "ttl_e", "ttl_dp", "ttl_tp", "ttl_fld", "ttl_tcg", "p_g", "p_tc", "p_sc", "p_po", "p_a",
			"p_e", "p_fld", "p_tcg", "c_g", "c_tc", "c_sc", "c_po", "c_a", "c_e", "c_fld", "c_tcg", "fbm_g", "fbm_tc",
			"fbm_sc", "fbm_po", "fbm_a", "fbm_e", "fbm_fld", "fbm_tcg", "sbm_g", "sbm_tc", "sbm_sc", "sbm_po", "sbm_a",
			"sbm_e", "sbm_fld", "sbm_tcg", "tbm_g", "tbm_tc", "tbm_sc", "tbm_po", "tbm_a", "tbm_e", "tbm_fld",
			"tbm_tcg", "ss_g", "ss_tc", "ss_sc", "ss_po", "ss_a", "ss_e", "ss_fld", "ss_tcg", "of_g", "of_tc", "of_sc",
			"of_po", "of_a", "of_e", "of_fld", "of_tcg", "sp_era", "sp_w", "sp_l", "sp_pct", "sp_g", "sp_gs", "sp_ip",
			"sp_r", "sp_er", "sp_hr", "sp_so", "sp_bb", "sp_ibb", "sp_hb", "sp_wp", "sp_bk", "sp_h", "sp_sv", "sp_cg"));

	@Query("SELECT avs.start FROM AvatarStat avs, Stat s WHERE avs.avatar = :id AND avs.stat = s AND s.reference = :stat")
	List<String> _getStat(@Param("id") Avatar avatar, @Param("stat") String stat);


	default String getCachedStat(Avatar avatar, String stat) {
		avatarStatCache.cleanupCache();
		ConcurrentHashMap<String, String> avatarStats = avatarStatCache.get(avatar.getId());
		String retValue = "";
		if (avatarStats == null) {
            loadStatsFromStorage(avatar);
        }
        avatarStats = avatarStatCache.get(avatar.getId());
        if (avatarStats != null) {
            retValue = avatarStats.get(stat);
        }
		return retValue;
	}

	default void loadStatsFromStorage(Avatar avatar) {
        List<String> avatarStatList = _getStat(avatar, "combine");
        ConcurrentHashMap<String, String> avatarMap = new ConcurrentHashMap<>();
        for (String avatarStat : avatarStatList) {
            List<String> stats = Arrays.asList(avatarStat.split(";"));
            for (String statItem : stats) {
                if (statItem != null && !statItem.isEmpty()) {
                    String[] statObj = statItem.split("~");
                    if (statObj != null && statObj.length == 2) {
                        avatarMap.put(statObj[0], statObj[1]);
                    } else if (statObj != null && statObj.length == 1) {
                        avatarMap.put(statObj[0], "");
                    }
                }
            }
        }
        avatarStatCache.add(avatar.getId(), avatarMap, 0);
    }

	default String getCachedRes(Avatar avatar, String res) {
		avatarResCache.cleanupCache();
		ConcurrentHashMap<String, String> avatarRes = avatarResCache.get(avatar.getId());
		String retItem = "0";
		if (avatarRes != null && !avatarResCache.isExpired(avatar.getId())) {
			retItem = avatarRes.get(res) == null ? "0" : avatarRes.get(res);
		} else {
			List<AvatarRes> result = _loadRes(avatar);
			avatarRes = new ConcurrentHashMap<String, String>();

			for (AvatarRes resItem  : result) {
				avatarRes.put(resItem.getRes().getReference(), resItem.getStart().toString());
			}
			retItem = avatarRes.get(res) == null ? "0" : avatarRes.get(res);
			avatarResCache.add(avatar.getId(), avatarRes, 0);
		}
		return retItem;
	}

	default String getStatUnfiltered(Avatar avatar, String stat) {
		String result = "";
		if (stat != null && stat.contains("earned")) {
			List<String> avatarStatList = _getStat(avatar, stat);
			if (avatarStatList != null && avatarStatList.size() > 0) {
				result = avatarStatList.get(0);
			}
		} else {
			result = getCachedStat(avatar, stat);
		}
		if (result != null) {
			return result;
		}
		return MISSING_STAT;
	}

	default String getStat(Avatar avatar, String stat) {
		if (SEASON_STATS.contains(stat) && avatar.isNoSeasonStats()) {
			return NO_SEASON_STAT;
		}
		return getStatUnfiltered(avatar, stat);
	}

	default Map<String, String> getStat(Avatar avatar) {
        if (avatarStatCache.get(avatar.getId()) == null) {
            loadStatsFromStorage(avatar);
        }
		return avatarStatCache.get(avatar.getId());
	}

	@Query("SELECT avs FROM AvatarStat avs, Stat s WHERE avs.avatar = :id AND avs.stat = s AND s.reference = :stat")
	List<AvatarStat> _loadStat(@Param("id") Avatar avatar, @Param("stat") String stat);

	default AvatarStat loadStat(Avatar avatar, String stat) {
		List<AvatarStat> result = _loadStat(avatar, stat);
		if ((result.size() > 0) && (result.get(0) != null)) {
			return result.get(0);
		}
		return new AvatarStat();
	}

	default AvatarStat setStat(Avatar avatar, String stat, Integer value) {
		return setStat(avatar, stat, value.toString());
	}

	default AvatarStat setStat(Avatar avatar, String stat, String value) {
		AvatarStat avrStat = loadStat(avatar, stat);
		avrStat.setStart(value);
		return avrStat;
	}

	@Query("SELECT avr.start FROM AvatarRes avr, Res r WHERE avr.avatar = :id AND avr.res = r AND r.reference = :res")
	List<Integer> _getRes(@Param("id") Avatar avatar, @Param("res") String res);

	default String getRes(Avatar avatar, String res) {
		List<Integer> result = _getRes(avatar, res);
		if ((result.size() > 0) && (result.get(0) != null)) {
			return result.get(0).toString();
		}
		return "0";
	}

	@Query("SELECT avr FROM AvatarRes avr, Res r WHERE avr.avatar = :id AND avr.res = r AND r.reference IN ('pitcher_hits', 'pitcher_bb', 'hitbatter', 'pitcher_runs')")
	List<AvatarRes> _getLob(@Param("id") Avatar avatar);
	
	default Integer getLob(Avatar avatar) { //pitcher_hits + pitcher_bb + hit_batter - pitcher_runs
		List<AvatarRes> allStats = _getLob(avatar);
		
		if (allStats == null || allStats.size() == 0) {
			return 0;
		}

		int lob = 0;
		for(AvatarRes res : allStats) {
			switch(res.getRes().getReference()) {
				case PITCHER_HITS_LONG:
				case PITCHER_BB_LONG:
				case HIT_BATTER_LONG:
					lob += res.getStart();
					break;
				case PITCHER_RUNS_LONG:
					lob -= res.getStart();
					break;
			}
		}
		
		return lob;
	}

	@Query("SELECT avr FROM AvatarRes avr, Res r WHERE avr.avatar = :id AND avr.res = r AND r.reference = :res")
	List<AvatarRes> _loadRes(@Param("id") Avatar avatar, @Param("res") String res);

	@Query("SELECT avr FROM AvatarRes avr, Res r WHERE avr.avatar = :id AND avr.res = r")
	List<AvatarRes> _loadRes(@Param("id") Avatar avatar);

	default AvatarRes loadRes(Avatar avatar, String res) {
		List<AvatarRes> result = _loadRes(avatar, res);
		if ((result.size() > 0) && (result.get(0) != null)) {
			return result.get(0);
		}
		return null;
	}

	default AvatarRes setRes(Avatar avatar, String res, String value) {
		return setRes(avatar, res, PHPHelper.toInt(value));
	}

	default void makeResExpire(Integer avatarId) {
		avatarResCache.expireCache(avatarId);
	}

	default AvatarRes setRes(Avatar avatar, String res, Integer value) {
		AvatarRes avrRes = loadRes(avatar, res);
		avrRes.setStart(value);
		avatarResCache.expireCache(avatar.getId());
		return avrRes;
	}

	default BigDecimal getResBigDecimalOrZero(Avatar avatar, String res) {
		String start = getCachedRes(avatar, res);
		BigDecimal bd = null;
		try {
			bd = NumberUtils.createBigDecimal(start);
		} catch (NumberFormatException e) {
			// Don't care - convert to zero below
		}
		return bd == null ? BigDecimal.ZERO : bd;
	}

	@Modifying
	@Transactional
	@Query(value = "delete av FROM avatar av, game_npc gamenpc1_, game_sector gamesector2_, game_cluster gamecluste3_ where gamenpc1_.avatar = av.id " +
        "and gamenpc1_.sector=gamesector2_.id AND gamesector2_.cluster=gamecluste3_.id AND gamecluste3_.game= :id", nativeQuery = true)
	void deleteAvatarsByGameId(@Param("id") Integer id);

}
