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
@Table(name = "avatar_dice")
public class AvatarDice {


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
     * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Dice")
	 * @ORM\JoinColumn(name="dice", referencedColumnName="id", onDelete="CASCADE")
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="dice")
    private Dice dice;

    @Column(name="priority")
	private Integer priority = 1;
	
    @Column(name="name")
	 private String name = "primary";

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

	public Dice getDice() {
		return dice;
	}

	public void setDice(Dice dice) {
		this.dice = dice;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    
	
}
