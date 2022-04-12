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
@Table(name = "AvatarStat")
public class AvatarStat {

    public static final Integer DEFAULT_START = -777;
    
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
     * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Stat")
	 * @ORM\JoinColumn(name="stat", referencedColumnName="id", onDelete="CASCADE")
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="stat")
    private Stat stat;

    @Column(name="start")
    private String start = DEFAULT_START.toString();

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
