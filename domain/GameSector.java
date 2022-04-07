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
@Table(name = "game_sector")
public class GameSector implements IdAble {

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
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameCluster")
	 * @ORM\JoinColumn(name="cluster", referencedColumnName="id", onDelete="CASCADE")
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cluster")
	private GameCluster cluster;

	@Column(name = "name")
	private String name;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public GameCluster getCluster() {
		return cluster;
	}

	public void setCluster(GameCluster cluster) {
		this.cluster = cluster;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
