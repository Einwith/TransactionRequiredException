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
@Table(name = "game_chat")
public class ChatMessage {

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
	
	@Column(name="username", nullable = false, length = 100)
	private String username;
	
	@Column(name="message", nullable = false, length = 1000)
    private String message;
	
	public ChatMessage() {}
	
	public ChatMessage(Game game, String username, String message) {
		this.game = game;
		this.username = username;
		this.message = message;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
