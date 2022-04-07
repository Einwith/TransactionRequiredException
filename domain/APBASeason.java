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
@Table(name = "apba_session")
public class APBASeason {

    @Id
    @GeneratedValue(
    		strategy = GenerationType.AUTO,
    		generator = "native"
    )
    @GenericGenerator(
    		name = "native",
    		strategy = "native"
    )
    private Integer id;
	
	/**
     *
     * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Client")
	 * @ORM\JoinColumn(name="client", referencedColumnName="id", onDelete="CASCADE")
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="client")
	private Client client;
	
    /**
     *
     * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Game")
	 * @ORM\JoinColumn(name="game", referencedColumnName="id", onDelete="CASCADE")
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="game")
    private Game game;
	
	/**
     *
     * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GamePlayer")
	 * @ORM\JoinColumn(name="player", referencedColumnName="id", onDelete="CASCADE")
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="player")
    private GamePlayer player;

    @Column(name="token")
	private String token;

    @Column(name="gametoken")
	private String gametoken;

    @Column(name="lastaction")
    private Integer lastaction = 0;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public GamePlayer getPlayer() {
		return player;
	}

	public void setPlayer(GamePlayer player) {
		this.player = player;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getGametoken() {
		return gametoken;
	}

	public void setGametoken(String gametoken) {
		this.gametoken = gametoken;
	}

	public Integer getLastaction() {
		return lastaction;
	}

	public void setLastaction(Integer lastaction) {
		this.lastaction = lastaction;
	}
        	
}
