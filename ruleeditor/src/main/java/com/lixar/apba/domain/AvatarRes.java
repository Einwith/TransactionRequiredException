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
@Table(name = "avatar_res")
public class AvatarRes {

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
     *
     * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Avatar")
	 * @ORM\JoinColumn(name="avatar", referencedColumnName="id", onDelete="CASCADE")
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="avatar")
    private Avatar avatar;
	
	/**
     *
     * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Res")
	 * @ORM\JoinColumn(name="res", referencedColumnName="id", onDelete="CASCADE")
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="res")
    private Res res;

    @Column(name="start")
    private Integer start;

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

	public Avatar getAvatar() {
		return avatar;
	}

	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

	public Res getRes() {
		return res;
	}

	public void setRes(Res res) {
		this.res = res;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}
}
