package com.lixar.apba.domain;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.lixar.apba.web.ModelConstants.Side;

@Entity
@Table(name = "ExternalGame")
public class ExternalGame {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,
    		generator = "native"
    )
    @GenericGenerator(
    		name = "native",
    		strategy = "native"
    )
	private Integer id;

	@Column(name = "ExtGid")
	private String extGid;

	@Column(name = "int_gid")
	private Integer intGid;

	@Column(name = "ext_p1")
	private Integer externalHomePlayer;

	@Column(name = "int_p1")
	private Integer externalHomePlayerId;

	@Column(name = "ext_p2")
	private Integer externalAwayPlayer;

	@Column(name = "int_p2")
	private Integer externalAwayPlayerId;

	@Column(name = "ext_t1")
	private Integer externalHomeTeam;

	@Column(name = "ext_t2")
	private Integer externalAwayTeam;

	@Column(name = "league_id")
	private Integer league_id;
	
	@Column(name = "home_guid", columnDefinition = "BINARY(16)")
	private UUID homeGUID;
	
	@Column(name = "away_guid", columnDefinition = "BINARY(16)")
	private UUID awayGUID;
	
	@Column(name = "tournament_id")
	private Long tournamentId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getExtGid() {
		return extGid;
	}

	public void setExtGid(String extGid) {
		this.extGid = extGid;
	}

	public Integer getIntGid() {
		return intGid;
	}

	public void setIntGid(Integer int_gid) {
		this.intGid = int_gid;
	}

	public Integer getExternalHomePlayer() {
		return externalHomePlayer;
	}

	public void setExternalHomePlayer(Integer externalHomePlayer) {
		this.externalHomePlayer = externalHomePlayer;
	}

	public Integer getExternalHomePlayerId() {
		return externalHomePlayerId;
	}

	public void setExternalHomePlayerId(Integer externalHomePlayerId) {
		this.externalHomePlayerId = externalHomePlayerId;
	}

	public Integer getExternalAwayPlayer() {
		return externalAwayPlayer;
	}

	public void setExternalAwayPlayer(Integer externalAwayPlayer) {
		this.externalAwayPlayer = externalAwayPlayer;
	}

	public Integer getExternalAwayPlayerId() {
		return externalAwayPlayerId;
	}

	public void setExternalAwayPlayerId(Integer externalAwayPlayerId) {
		this.externalAwayPlayerId = externalAwayPlayerId;
	}

	public Integer getExternalHomeTeam() {
		return externalHomeTeam;
	}

	public void setExternalHomeTeam(Integer externalHomeTeam) {
		this.externalHomeTeam = externalHomeTeam;
	}

	public Integer getExernalAwayTeam() {
		return externalAwayTeam;
	}

	public void setExternalAwayTeam(Integer externalAwayTeam) {
		this.externalAwayTeam = externalAwayTeam;
	}

	public Integer getLeague_id() {
		return league_id;
	}

	public void setLeague_id(Integer league_id) {
		this.league_id = league_id;
	}

	public UUID getHomeGUID() {
		return homeGUID;
	}

	public void setHomeGUID(UUID homeGUID) {
		this.homeGUID = homeGUID;
	}

	public UUID getAwayGUID() {
		return awayGUID;
	}

	public void setAwayGUID(UUID awayGUID) {
		this.awayGUID = awayGUID;
	}
	
	public Long getTournamentId() {
		return tournamentId;
	}
	
	public void setTournamentId(Long tournamentId) {
		this.tournamentId = tournamentId;
	}

	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away Player
	 */
	public Integer getExternalPlayer(Side teamSide) {
		if (teamSide == Side.HOME) {
			return externalHomePlayer;
		} else {
			return externalAwayPlayer;
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param player
	 * @param teamSide
	 */
	public void setExternalPlayer(Integer player, Side teamSide) {
		if (teamSide == Side.HOME) {
			setExternalHomePlayer(player);
		} else {
			setExternalAwayPlayer(player);
		}
	}

	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away Player Id
	 */
	public Integer getExternalPlayerId(Side teamSide) {
		if (teamSide == Side.HOME) {
			return externalHomePlayerId;
		} else {
			return externalAwayPlayerId;
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param playerId
	 * @param teamSide
	 */
	public void setExternalPlayerId(Integer playerId, Side teamSide) {
		if (teamSide == Side.HOME) {
			setExternalHomePlayerId(playerId);
		} else {
			setExternalAwayPlayerId(playerId);
		}
	}

	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away Team
	 */
	public Integer getExternalTeam(Side teamSide) {
		if (teamSide == Side.HOME) {
			return externalHomeTeam;
		} else {
			return externalAwayTeam;
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param externalTeam 
	 * @param teamSide
	 */
	public void setExternalTeam(Integer externalTeam, Side teamSide) {
		if (teamSide == Side.HOME) {
			setExternalHomeTeam(externalTeam);
		} else {
			setExternalAwayTeam(externalTeam);
		}
	}

	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away GUID
	 */
	public UUID getGUID(Side teamSide) {
		if (teamSide == Side.HOME) {
			return homeGUID;
		} else {
			return awayGUID;
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param guid
	 * @param teamSide
	 */
	public void setGUID(UUID guid, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeGUID(guid);
		} else {
			setAwayGUID(guid);
		}
	}
}
