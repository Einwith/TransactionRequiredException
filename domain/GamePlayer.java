package com.lixar.apba.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "game_player")
public class GamePlayer implements IdAble {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,
    		generator = "native"
    )
    @GenericGenerator(
    		name = "native",
    		strategy = "native"
    )
	private Integer id;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameSector")
	 * @ORM\JoinColumn(name="sector", referencedColumnName="id", onDelete="CASCADE")
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sector")
	private GameSector sector;

	@Column(name = "player")
	private Integer player;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "lineup_order")
	private String lineupOrder;

	@Column(name = "team_id")
	private Integer team_id;
	
	@Column(name = "firebase_token")
	private String firebaseToken;
	
	@Column(name = "firebase_token_issued")
	private Date firebaseTokenIssued;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public GameSector getSector() {
		return sector;
	}

	public void setSector(GameSector sector) {
		this.sector = sector;
	}

	public Integer getPlayer() {
		return player;
	}

	public void setPlayer(Integer player) {
		this.player = player;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getLineupOrder() {
		return lineupOrder;
	}

	public void setLineupOrder(String destination) {
		this.lineupOrder = destination;
	}

	public Integer getTeam_id() {
		return team_id;
	}

	public void setTeam_id(Integer team_id) {
		this.team_id = team_id;
	}

	public String getFirebaseToken() {
		return firebaseToken;
	}

	public void setFirebaseToken(String firebaseToken) {
		this.firebaseToken = firebaseToken;
	}
	
	public Date getFirebaseTokenIssued() {
		return firebaseTokenIssued;
	}

	public void setFirebaseTokenIssued(Date firebaseTokenIssued) {
		this.firebaseTokenIssued = firebaseTokenIssued;
	}
}
