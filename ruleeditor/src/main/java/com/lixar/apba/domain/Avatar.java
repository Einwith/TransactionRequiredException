package com.lixar.apba.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "Avatar")
public class Avatar implements IdAble {

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

    @Column(name="first_name", length = 60)
    private String firstName;
    
    @Column(name="last_name", length = 60)
    private String lastName;

    @Column(name="start")
    private Boolean start;

	@Column(name = "no_season_stats", nullable = false)
	private boolean noSeasonStats;

    @OneToMany(mappedBy = "avatar")
    private Set<AvatarRes> res;

    public Set<AvatarStat> getStat() {
        return stat;
    }

    @OneToMany(mappedBy = "avatar")
    private Set<AvatarStat> stat;

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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String name) {
		this.firstName = name;
	}
	
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String name) {
		this.lastName = name;
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
	}

	public Boolean getStart() {
		return start;
	}

	public void setStart(Boolean start) {
		this.start = start;
	}

	public boolean isNoSeasonStats() {
		return noSeasonStats;
	}

	public void setNoSeasonStats(boolean noSeasonStats) {
		this.noSeasonStats = noSeasonStats;
	}
        
}
