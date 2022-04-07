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
@Table(name = "Game")
public class Game {

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
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Client")
	 * @ORM\JoinColumn(name="client", referencedColumnName="id", onDelete="CASCADE")
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "client")
	private Client client;

	@Column(name = "start_date")
	private Integer startDate;

	@Column(name = "ranked", nullable = false)
	private boolean ranked = false;

	@Column(name = "cluster_nb")
	private Integer cluster_nb;

	@Column(name = "sector_nb")
	private Integer sector_nb;

	@Column(name = "status")
	private Integer status;

	@Column(name = "name")
	private String name;

	@Column(name = "ready")
	private Integer ready;

	@Column(name = "started")
	private Integer started;

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

	public Integer getStartDate() {
		return startDate;
	}

	public void setStartDate(Integer start_date) {
		this.startDate = start_date;
	}

	public boolean getRanked() {
		return ranked;
	}

	public void setRanked(boolean ranked) {
		this.ranked = ranked;
	}

	public Integer getCluster_nb() {
		return cluster_nb;
	}

	public void setCluster_nb(Integer cluster_nb) {
		this.cluster_nb = cluster_nb;
	}

	public Integer getSector_nb() {
		return sector_nb;
	}

	public void setSector_nb(Integer sector_nb) {
		this.sector_nb = sector_nb;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getReady() {
		return ready;
	}

	public void setReady(Integer ready) {
		this.ready = ready;
	}

	public Integer getStarted() {
		return started;
	}

	public void setStarted(Integer started) {
		this.started = started;
	}
}
