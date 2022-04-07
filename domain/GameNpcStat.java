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
@Table(name = "game_npc_stat")
public class GameNpcStat {

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
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="npc", referencedColumnName="id", onDelete="CASCADE")
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="npc")
    private GameNpc npc;

	/**
	 *
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Stat")
	 * @ORM\JoinColumn(name="stat", referencedColumnName="id", onDelete="CASCADE")
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="stat")
    private Stat stat;

    @Column(name="start")
    private String start = "-777";

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public GameNpc getNpc() {
		return npc;
	}

	public void setNpc(GameNpc npc) {
		this.npc = npc;
	}

	public Stat getStat() {
		return stat;
	}

	public void setStat(Stat stat) {
		this.stat = stat;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}


}
