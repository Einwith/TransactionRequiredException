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
@Table(name = "board_tile")
public class BoardTile {

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
     *
     * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Client")
	 * @ORM\JoinColumn(name="client", referencedColumnName="id", onDelete="CASCADE")
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="client")
	private Client client;
	
	/**
     * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Board")
	 * @ORM\JoinColumn(name="board", referencedColumnName="id", onDelete="CASCADE")
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="board")
    private Board board;

    @Column(name="sid")
    private Integer sid;

    @Column(name="name")
    private String name;

    @Column(name="html_class")
    private String html_class;

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

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHtml_class() {
		return html_class;
	}

	public void setHtml_class(String html_class) {
		this.html_class = html_class;
	}
    
    
    
}
