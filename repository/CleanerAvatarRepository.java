package com.lixar.apba.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lixar.apba.domain.CleanerAvatar;

public interface CleanerAvatarRepository extends JpaRepository<CleanerAvatar, Integer> {

	CleanerAvatar findOneByGid(int gid);
	
	default void removeAvatarsByGame(int gid) {
		CleanerAvatar cleanerAvatar = findOneByGid(gid);
		if (cleanerAvatar != null) {
			removeAvatars(cleanerAvatar.getAvatarIdsArray());
			removeAvatarDice(cleanerAvatar.getAvatarIdsArray());
			removeAvatarRes(cleanerAvatar.getAvatarIdsArray());
			removeAvatarStat(cleanerAvatar.getAvatarIdsArray());
			delete(cleanerAvatar);
		}
	}

	@Modifying
	@Query("DELETE FROM Avatar a WHERE a.id IN (:ids)")
	public void removeAvatars(@Param("ids") int[] ids);

	@Modifying
	@Query("DELETE FROM AvatarDice ad WHERE ad.avatar.id IN (:ids)")
	public void removeAvatarDice(@Param("ids") int[] ids);

	@Modifying
	@Query("DELETE FROM AvatarRes ar WHERE ar.avatar.id IN (:ids)")
	public void removeAvatarRes(@Param("ids") int[] ids);

	@Modifying
	@Query("DELETE FROM AvatarStat a WHERE a.avatar.id IN (:ids)")
	public void removeAvatarStat(@Param("ids") int[] ids);
}
