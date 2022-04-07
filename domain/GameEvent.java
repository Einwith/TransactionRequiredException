package com.lixar.apba.domain;

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
@Table(name = "game_event")
public class GameEvent {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO,
    		generator = "native"
    )
    @GenericGenerator(
    		name = "native",
    		strategy = "native"
    )
    private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="game", nullable = false)
	private Game game;
	
	@Column(name="type", nullable = false)
	private byte type;
	
	/**
	 * Flag indicates that event should be sent to home user
	 */
	@Column(name="home", nullable = false)
    private boolean home;
	
	/**
	 * Flag indicates that event should be sent to away user
	 */
	@Column(name="away", nullable = false)
    private boolean away;
	
	public GameEvent() {}
	
	public GameEvent(Game game, byte type, boolean isAI) {
		this.game = game;
		this.type = type;
		home = true;
		away = !isAI;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public boolean isHome() {
		return home;
	}

	public void setHome(boolean home) {
		this.home = home;
	}

	public boolean isAway() {
		return away;
	}

	public void setAway(boolean away) {
		this.away = away;
	}
}
