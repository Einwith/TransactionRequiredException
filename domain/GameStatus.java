package com.lixar.apba.domain;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Objects;

@Entity
@Table(name = "game_status")
@DynamicUpdate
public class GameStatus {

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
	 * Game that this status refers to
	 *
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Game")
	 * @ORM\JoinColumn(name="game", referencedColumnName="id", onDelete="CASCADE")
	 */
	@Column(name = "game")
	private Integer game;

	/**
	 * Current version of this status - is automatically incremented on every save
	 *
	 * @var integer $version
	 * @ORM\Column(type="integer")
	 * @ORM\Version
	 */
	//TODO: Check with Jonathan to see if anything special needs doing here
	@Column(name = "version")
	@Version
	private Integer version = 1;

	/**
	 * Last update in milliseconds since 1970 (microtime(true)*1000) - used to force a flush
	 *
	 * @var bigint $lastupdate
	 * @ORM\Column(name="lastupdate", type="bigint")
	 */
	@Column(name = "lastupdate")
	private Long lastUpdate = 0L;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGame() {
		return game;
	}

	public void setGame(Integer game) {
		this.game = game;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void setLastUpdateToNow() {
		Long currentLastUpdate = this.getLastUpdate();

		Long milliseconds = System.currentTimeMillis();

		if (milliseconds.equals(currentLastUpdate)) {
			milliseconds++;
		}

		this.setLastUpdate(milliseconds);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameStatus that = (GameStatus) o;
        return id.equals(that.id) &&
            game.equals(that.game) &&
            version.equals(that.version) &&
            lastUpdate.equals(that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, game, version, lastUpdate);
    }
}
