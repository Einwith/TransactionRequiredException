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

/**
 * Log of substitutions, excluding DH position.
 */
@Entity
@Table(name = "game_substitution")
public class GameSubstitution {

    /**
     * @var integer $id
     *
     * @ORM\Column(name="id", type="integer")
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
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

    /**
     *
     * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Avatar")
     * @ORM\JoinColumn(name="avatar_from", referencedColumnName="id", onDelete="CASCADE")
     */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "avatar_from")
	private Avatar avatarFrom;

    /**
    *
    * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Avatar")
    * @ORM\JoinColumn(name="avatar_to", referencedColumnName="id", onDelete="CASCADE")
    */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "avatar_to")
	private Avatar avatarTo;

    /**
     * @var string $position
     *
     * @ORM\Column(name="position", type="string", length=10)
     */
	@Column(name="position", length=10, nullable=false)
    private String position;

    /**
     * @var string $pinch
     *
     * @ORM\Column(name="pinch", type="string", length=2, nullable=true)
     */
	@Column(name="pinch", length=2)
    private String pinch;

    /**
     * @var integer $inning
     *
     * @ORM\Column(name="inning", type="integer")
     */
	@Column(name="inning", nullable=false)
    private int inning;

    /**
     * @var boolean is_substituted
     *
     * @ORM\Column(name="is_substituted", type="boolean")
     */
	@Column(name="is_substituted", nullable=false)
    private boolean isSubstituted;

    @Column(name = "tying_run_on_field", nullable = false)
    private boolean tyingRunOnField;

    @Column(name = "lead_less_than_three_runs", nullable = false)
    private boolean leadLessThanThreeRuns;

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Avatar getAvatarFrom() {
		return avatarFrom;
	}

	public void setAvatarFrom(Avatar avatarFrom) {
		this.avatarFrom = avatarFrom;
	}

	public Avatar getAvatarTo() {
		return avatarTo;
	}

	public void setAvatarTo(Avatar avatarTo) {
		this.avatarTo = avatarTo;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getPinch() {
		return pinch;
	}

	public void setPinch(String pinch) {
		this.pinch = pinch;
	}

	public int getInning() {
		return inning;
	}

	public void setInning(int inning) {
		this.inning = inning;
	}

	public boolean getIsSubstituted() {
		return isSubstituted;
	}

	public void setIsSubstituted(boolean isSubstituted) {
		this.isSubstituted = isSubstituted;
	}

    public boolean isTyingRunOnField() {
        return tyingRunOnField;
    }

    public void setTyingRunOnField(boolean potentialSave) {
        this.tyingRunOnField = potentialSave;
    }

    public boolean isLeadLessThanThreeRuns() {
        return leadLessThanThreeRuns;
    }

    public void setLeadLessThanThreeRuns(boolean leadLessThanThreeRuns) {
        this.leadLessThanThreeRuns = leadLessThanThreeRuns;
    }
}
