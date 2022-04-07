package com.lixar.apba.domain;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "game_npc")
@DynamicUpdate
public class GameNpc implements IdAble {

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
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameSector")
	 * @ORM\JoinColumn(name="sector", referencedColumnName="id", onDelete="CASCADE")
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sector")
	private GameSector sector;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Avatar")
	 * @ORM\JoinColumn(name="avatar", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "avatar")
	private Avatar avatar;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GamePlayer")
	 * @ORM\JoinColumn(name="player", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "player")
	private GamePlayer player;

    @Transient
    private String ratingStat;

    @Column(name = "first_name", length = 60)
    private String firstName;

    @Column(name = "last_name", length = 60)
    private String lastName;
    
    @Column(name="new_pitcher")
    private Boolean newPitcher;
    
    @Column(name="pitcher_grade_increased")
    private Boolean pitcherGradeIncreased;
    
    @Column(name="benched")
    private Boolean benched;
    
    @Column(name="relief_pitcher")
    private Boolean reliefPitcher;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public GameSector getSector() {
		return sector;
	}

	public void setSector(GameSector sector) {
		this.sector = sector;
	}

	public Avatar getAvatar() {
		return avatar;
	}

	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

	public GamePlayer getPlayer() {
		return player;
	}

	public void setPlayer(GamePlayer player) {
		this.player = player;
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

	/**
     * Get concat with space "first_name last_name"
     * @return full name
     */
	public String getFullName() {
		return firstName + " " + lastName;
	}

	/**
     * Get concat with comma "last_name, first_name"
     * @return full name
     */
	public String getFullNameReversed() {
		return lastName + ", " + firstName;
	}

	public Boolean isNewPitcher() {
		return newPitcher;
	}

	public void setNewPitcher(Boolean newPitcher) {
		this.newPitcher = newPitcher;
	}

    public Boolean isBenched() {
		return benched;
	}

	public void setBenched(Boolean benched) {
		this.benched = benched;
	}

	public String getRatingStat() {
        return ratingStat;
    }

    public void setRatingStat(String ratingStat) {
        this.ratingStat = ratingStat;
    }

    public Boolean isPitcherGradeIncreased() {
		return pitcherGradeIncreased;
	}

	public void setPitcherGradeIncreased(Boolean pitcherGradeIncreased) {
		this.pitcherGradeIncreased = pitcherGradeIncreased;
	}

	public Boolean isReliefPitcher() {
		return reliefPitcher;
	}

	public void setReliefPitcher(Boolean reliefPitcher) {
		this.reliefPitcher = reliefPitcher;
	}

	@Override
    public boolean equals(Object npc) {
		if (this == npc) return true;
        if (!(npc instanceof GameNpc)) {
            return false;
        }
        GameNpc gameNpc = (GameNpc) npc;
        return id.equals(gameNpc.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GameNpc{");
        sb.append("fullName='").append(getFullName()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
