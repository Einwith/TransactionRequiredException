package com.lixar.apba.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

import com.lixar.apba.core.util.PHPHelper;
import com.lixar.apba.web.ModelConstants.Side;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "game_ready")
@DynamicUpdate
public class GameReady {

	/**
	 * Replacement constants notes:
	 * Values changed to make them independent and use bitwise operations. They are incompatible with old code.
	 * *_PITCHER constants actually needed only for front-end logic (beginning of GameService.response method)
	 * So set REPLACEMENT_ADVICE_PITCHER and REPLACEMENT_ADVICE (REPLACEMENT_INJURED and REPLACEMENT_INJURED_PITCHER)
	 * depending on position and clear be calling deleteAdviceReplacementMarks or deleteInjuryReplacementMarks
	 */
	public static final int REPLACEMENT_UNNECESSARY = 0;
	/**
	 * @see com.lixar.apba.domain.GameReady#REPLACEMENT_UNNECESSARY
	 */
	public static final int REPLACEMENT_ADVICE = 8;
	/**
	 * @see com.lixar.apba.domain.GameReady#REPLACEMENT_UNNECESSARY
	 */
	public static final int REPLACEMENT_ADVICE_PITCHER = 16;
	/**
	 * @see com.lixar.apba.domain.GameReady#REPLACEMENT_UNNECESSARY
	 */
	public static final int REPLACEMENT_INJURED = 32;
	/**
	 * @see com.lixar.apba.domain.GameReady#REPLACEMENT_UNNECESSARY
	 */
	public static final int REPLACEMENT_INJURED_PITCHER = 64;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,
    		generator = "native"
    )
    @GenericGenerator(
    		name = "native",
    		strategy = "native"
    )
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "client")
	private Client client;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\Game")
	 * @ORM\JoinColumn(name="game", referencedColumnName="id", onDelete="CASCADE")
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game")
	private Game game;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GamePlayer")
	 * @ORM\JoinColumn(name="home_player", referencedColumnName="id", onDelete="CASCADE")
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_player")
	private GamePlayer homePlayer;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GamePlayer")
	 * @ORM\JoinColumn(name="away_player", referencedColumnName="id", onDelete="CASCADE")
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "away_player")
	private GamePlayer awayPlayer;

	/**
	 * @var boolean $homeLineup
	 * @ORM\Column(name="home_lineup", type="boolean", nullable=true)
	 */
	@Column(name = "home_lineup")
	private Boolean homeLineup;

	/**
	 * @var boolean $awayLineup
	 * @ORM\Column(name="away_lineup", type="boolean", nullable=true)
	 */
	@Column(name = "away_lineup")
	private Boolean awayLineup;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="home_pitcher", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_pitcher")
	private GameNpc homePitcher;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="home_catcher", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_catcher")
	private GameNpc homeCatcher;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="home_first", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_first")
	private GameNpc homeFirst;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="home_second", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_second")
	private GameNpc homeSecond;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="home_third", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_third")
	private GameNpc homeThird;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="home_short", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_short")
	private GameNpc homeShort;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="home_left", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_left")
	private GameNpc homeLeft;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="home_center", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_center")
	private GameNpc homeCenter;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="home_right", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_right")
	private GameNpc homeRight;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="home_hitter", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_hitter")
	private GameNpc homeHitter;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="away_pitcher", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "away_pitcher")
	private GameNpc awayPitcher;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="away_catcher", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "away_catcher")
	private GameNpc awayCatcher;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="away_first", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "away_first")
	private GameNpc awayFirst;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="away_second", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "away_second")
	private GameNpc awaySecond;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="away_third", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "away_third")
	private GameNpc awayThird;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="away_short", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "away_short")
	private GameNpc awayShort;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="away_left", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "away_left")
	private GameNpc awayLeft;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="away_center", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "away_center")
	private GameNpc awayCenter;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="away_right", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "away_right")
	private GameNpc awayRight;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="away_hitter", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "away_hitter")
	private GameNpc awayHitter;

	@Column(name = "inning", nullable = false)
	private int inning = 0;

	@Column(name = "inning_top", nullable = false)
	private boolean inningTop = false;

	@Column(name = "home_score", nullable = false)
	private int home = 0;

	@Column(name = "away_score", nullable = false)
	private int away = 0;

	@Column(name = "home_inningscore", nullable = false)
	private String home_inningscore = "";

	@Column(name = "away_inningscore", nullable = false)
	private String away_inningscore = "";

	@Column(name = "lead", nullable = false)
	private int lead = 0;

	@Column(name = "away_errors", nullable = false)
	private int away_errors = 0;

	@Column(name = "away_hits", nullable = false)
	private int away_hits = 0;

	@Column(name = "home_errors", nullable = false)
	private int home_errors = 0;

	@Column(name = "home_hits", nullable = false)
	private int home_hits = 0;

	@Column(name = "cso", nullable = false)
	private int cso = 0;

	@Column(name = "strikes", nullable = false)
	private int strikes = 0;

	@Column(name = "balls", nullable = false)
	private int balls = 0;

    @Column(name = "home_micro_manager_id", nullable = false)
    private Integer homeMicroManagerId;

    @Column(name = "away_micro_manager_id", nullable = false)
    private Integer awayMicroManagerId;
    
    @Column(name = "home_team_year")
    private String homeTeamYear;
    
    @Column(name = "away_team_year")
    private String awayTeamYear;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="batter_1", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "batter_1", referencedColumnName = "id")
	private GameNpc batter_1;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="batter_2", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "batter_2", referencedColumnName = "id")
	private GameNpc batter_2;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="batter_3", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "batter_3", referencedColumnName = "id")
	private GameNpc batter_3;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="batter_4", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "batter_4", referencedColumnName = "id")
	private GameNpc batter_4;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="batter_5", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "batter_5", referencedColumnName = "id")
	private GameNpc batter_5;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="batter_6", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "batter_6", referencedColumnName = "id")
	private GameNpc batter_6;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="batter_7", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "batter_7", referencedColumnName = "id")
	private GameNpc batter_7;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="batter_8", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "batter_8", referencedColumnName = "id")
	private GameNpc batter_8;

	/**
	 * @ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * @ORM\JoinColumn(name="batter_9", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "batter_9", referencedColumnName = "id")
	private GameNpc batter_9;

	@Column(name = "batting_position")
	private Integer batting_position;

	@Column(name = "batting_away")
	private Integer batting_away;

	@Column(name = "batting_home")
	private Integer batting_home;

	@Column(name = "home_team")
	private String home_team;

	@Column(name = "away_team")
	private String away_team;

	@Column(name = "home_abbr")
	private String home_abbr;

	@Column(name = "away_abbr")
	private String away_abbr;

	@Column(name = "r1_held")
	private String r1_held = "ho";

	@Column(name = "r3_held")
	private String r3_held = "nh";

	@Column(name = "game_infield", nullable = false)
	private String game_infield = "d";

	@Column(name = "playsafer1", nullable = false)
	private boolean playsafer1 = false;

	@Column(name = "playsafer2", nullable = false)
	private boolean playsafer2 = false;

	@Column(name = "playsafer3", nullable = false)
	private boolean playsafer3 = false;

	@Column(name = "stretch", nullable = false)
	private String stretch = "";

	/**
	 * ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * ORM\JoinColumn(name="winning_pitcher", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "winning_pitcher", referencedColumnName = "id")
	private GameNpc winning_pitcher;

	/**
	 * ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * ORM\JoinColumn(name="losing_pitcher", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "losing_pitcher", referencedColumnName = "id")
	private GameNpc losing_pitcher;

	/**
	 * ORM\ManyToOne(targetEntity="Stratdgi\EngineBundle\Entity\GameNpc")
	 * ORM\JoinColumn(name="save_pitcher", referencedColumnName="id", onDelete="CASCADE", nullable=true)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "save_pitcher", referencedColumnName = "id")
	private GameNpc save_pitcher;

	@Column(name = "home_pitcher_sub", nullable = false)
	private int homePitcherSub = 0;

	@Column(name = "away_pitcher_sub", nullable = false)
	private int awayPitcherSub = 0;

	@Column(name = "game_active", nullable = false)
	private boolean gameActive = true;

	@Column(name = "next_command")
	private String next_command = "";

	@Column(name = "pitch", nullable = false)
	private boolean pitch = false;

	@Column(name = "forfeit", nullable = false)
	private int forfeit = 0;

	@Column(name = "busy", nullable = false)
	private String busy = "0";

	@Column(name = "busy_delay", nullable = false)
	private int busy_delay = 5;

	@Column(name = "first_batter_of_inning", nullable = false)
	private boolean firstBatterOfInning = false;

	@Column(name = "beginning_of_inning", nullable = false)
	private boolean beginningOfInning = false;

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

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public GamePlayer getHomePlayer() {
		return homePlayer;
	}

	public void setHomePlayer(GamePlayer homePlayer) {
		this.homePlayer = homePlayer;
	}

	public GamePlayer getAwayPlayer() {
		return awayPlayer;
	}

	public void setAwayPlayer(GamePlayer awayPlayer) {
		this.awayPlayer = awayPlayer;
	}

	public Boolean getHomeLineup() {
		return homeLineup;
	}

	public void setHomeLineup(Boolean homeLineup) {
		this.homeLineup = homeLineup;
	}

	public Boolean getAwayLineup() {
		return awayLineup;
	}

	public void setAwayLineup(Boolean awayLineup) {
		this.awayLineup = awayLineup;
	}

	public GameNpc getHomePitcher() {
		return homePitcher;
	}

	public void setHomePitcher(GameNpc homePitcher) {
		this.homePitcher = homePitcher;
	}

	public GameNpc getHomeCatcher() {
		return homeCatcher;
	}

	public void setHomeCatcher(GameNpc homeCatcher) {
		this.homeCatcher = homeCatcher;
	}

	public GameNpc getHomeFirst() {
		return homeFirst;
	}

	public void setHomeFirst(GameNpc homeFirst) {
		this.homeFirst = homeFirst;
	}

	public GameNpc getHomeSecond() {
		return homeSecond;
	}

	public void setHomeSecond(GameNpc homeSecond) {
		this.homeSecond = homeSecond;
	}

	public GameNpc getHomeThird() {
		return homeThird;
	}

	public void setHomeThird(GameNpc homeThird) {
		this.homeThird = homeThird;
	}

	public GameNpc getHomeShort() {
		return homeShort;
	}

	public void setHomeShort(GameNpc homeShort) {
		this.homeShort = homeShort;
	}

	public GameNpc getHomeLeft() {
		return homeLeft;
	}

	public void setHomeLeft(GameNpc homeLeft) {
		this.homeLeft = homeLeft;
	}

	public GameNpc getHomeCenter() {
		return homeCenter;
	}

	public void setHomeCenter(GameNpc homeCenter) {
		this.homeCenter = homeCenter;
	}

	public GameNpc getHomeRight() {
		return homeRight;
	}

	public void setHomeRight(GameNpc homeRight) {
		this.homeRight = homeRight;
	}

	public GameNpc getHomeHitter() {
		return homeHitter;
	}

	public void setHomeHitter(GameNpc homeHitter) {
		this.homeHitter = homeHitter;
	}

	public GameNpc getAwayPitcher() {
		return awayPitcher;
	}

	public void setAwayPitcher(GameNpc awayPitcher) {
		this.awayPitcher = awayPitcher;
	}

	public GameNpc getAwayCatcher() {
		return awayCatcher;
	}

	public void setAwayCatcher(GameNpc awayCatcher) {
		this.awayCatcher = awayCatcher;
	}

	public GameNpc getAwayFirst() {
		return awayFirst;
	}

	public void setAwayFirst(GameNpc awayFirst) {
		this.awayFirst = awayFirst;
	}

	public GameNpc getAwaySecond() {
		return awaySecond;
	}

	public void setAwaySecond(GameNpc awaySecond) {
		this.awaySecond = awaySecond;
	}

	public GameNpc getAwayThird() {
		return awayThird;
	}

	public void setAwayThird(GameNpc awayThird) {
		this.awayThird = awayThird;
	}

	public GameNpc getAwayShort() {
		return awayShort;
	}

	public void setAwayShort(GameNpc awayShort) {
		this.awayShort = awayShort;
	}

	public GameNpc getAwayLeft() {
		return awayLeft;
	}

	public void setAwayLeft(GameNpc awayLeft) {
		this.awayLeft = awayLeft;
	}

	public GameNpc getAwayCenter() {
		return awayCenter;
	}

	public void setAwayCenter(GameNpc awayCenter) {
		this.awayCenter = awayCenter;
	}

	public GameNpc getAwayRight() {
		return awayRight;
	}

	public void setAwayRight(GameNpc awayRight) {
		this.awayRight = awayRight;
	}

	public GameNpc getAwayHitter() {
		return awayHitter;
	}

	public void setAwayHitter(GameNpc awayHitter) {
		this.awayHitter = awayHitter;
	}

	public int getInning() {
		return inning;
	}

	public void setInning(int inning) {
		this.inning = inning;
	}

	public boolean getInningTop() {
		return inningTop;
	}

	public void setInningTop(boolean inning_top) {
		this.inningTop = inning_top;
	}

	public Integer getHome() {
		return home;
	}

	public void setHome(Integer home) {
		this.home = home;
	}

	public Integer getAway() {
		return away;
	}

	public void setAway(Integer away) {
		this.away = away;
	}

    public int getAiRuns(HttpSession session, GameReady gameReady) {
        if ((Boolean) session.getAttribute("playerhome")) {
            return gameReady.getHome();
        } else {
            return gameReady.getAway();
        }
    }

	public String getHomeInningscore() {
		return home_inningscore;
	}

	public void setHomeInningscore(String home_inningscore) {
		this.home_inningscore = home_inningscore;
	}

	public String getAwayInningscore() {
		return away_inningscore;
	}

	public void setAwayInningscore(String away_inningscore) {
		this.away_inningscore = away_inningscore;
	}

	public int getLead() {
		return lead;
	}

	public void setLead(int lead) {
		this.lead = lead;
	}

	public int getAwayErrors() {
		return away_errors;
	}

	public void setAwayErrors(int away_errors) {
		this.away_errors = away_errors;
	}

	public int getAwayHits() {
		return away_hits;
	}

	public void setAwayHits(int away_hits) {
		this.away_hits = away_hits;
	}

	public int getHomeErrors() {
		return home_errors;
	}

	public void setHomeErrors(int home_errors) {
		this.home_errors = home_errors;
	}

	public int getHomeHits() {
		return home_hits;
	}

	public void setHomeHits(int home_hits) {
		this.home_hits = home_hits;
	}

	public int getCso() {
		return cso;
	}

	public void setCso(int cso) {
		this.cso = cso;
	}

	public int getStrikes() {
		return strikes;
	}

	public void setStrikes(int strikes) {
		this.strikes = strikes;
	}

	public int getBalls() {
		return balls;
	}

	public void setBalls(int balls) {
		this.balls = balls;
	}

	public GameNpc getBatter1() {
		return batter_1;
	}

	public void setBatter1(GameNpc batter_1) {
		this.batter_1 = batter_1;
	}

	public GameNpc getBatter2() {
		return batter_2;
	}

	public void setBatter2(GameNpc batter_2) {
		this.batter_2 = batter_2;
	}

	public GameNpc getBatter3() {
		return batter_3;
	}

	public void setBatter3(GameNpc batter_3) {
		this.batter_3 = batter_3;
	}

	public GameNpc getBatter4() {
		return batter_4;
	}

	public void setBatter4(GameNpc batter_4) {
		this.batter_4 = batter_4;
	}

	public GameNpc getBatter5() {
		return batter_5;
	}

	public void setBatter5(GameNpc batter_5) {
		this.batter_5 = batter_5;
	}

	public GameNpc getBatter6() {
		return batter_6;
	}

	public void setBatter6(GameNpc batter_6) {
		this.batter_6 = batter_6;
	}

	public GameNpc getBatter7() {
		return batter_7;
	}

	public void setBatter7(GameNpc batter_7) {
		this.batter_7 = batter_7;
	}

	public GameNpc getBatter8() {
		return batter_8;
	}

	public void setBatter8(GameNpc batter_8) {
		this.batter_8 = batter_8;
	}

	public GameNpc getBatter9() {
		return batter_9;
	}

	public void setBatter9(GameNpc batter_9) {
		this.batter_9 = batter_9;
	}

	public Integer getBattingPosition() {
		return batting_position;
	}

	public void setBattingPosition(Integer batting_position) {
		this.batting_position = batting_position;
	}

	public Integer getBattingAway() {
		return batting_away;
	}

	public void setBattingAway(Integer batting_away) {
		this.batting_away = batting_away;
	}

	public Integer getBattingHome() {
		return batting_home;
	}

	public void setBattingHome(Integer batting_home) {
		this.batting_home = batting_home;
	}

	public String getHomeTeam() {
		return home_team;
	}

	public void setHomeTeam(String home_team) {
		this.home_team = home_team;
	}

	public String getAwayTeam() {
		return away_team;
	}

	public void setAwayTeam(String away_team) {
		this.away_team = away_team;
	}

	public String getHomeAbbr() {
		return home_abbr;
	}

	public void setHomeAbbr(String home_abbr) {
		this.home_abbr = home_abbr;
	}

	public String getAwayAbbr() {
		return away_abbr;
	}

	public void setAwayAbbr(String away_abbr) {
		this.away_abbr = away_abbr;
	}

	public String getR1Held() {
		return r1_held;
	}

	public void setR1Held(String r1_held) {
		this.r1_held = r1_held;
	}

	public String getR3Held() {
		return r3_held;
	}

	public void setR3Held(String r3_held) {
		this.r3_held = r3_held;
	}

	public String getGameInfield() {
		return game_infield;
	}

	public void setGameInfield(@NotNull String game_infield) {
		this.game_infield = game_infield;
	}

	public boolean getPlaySafeR1() {
		return playsafer1;
	}

	public void setPlaySafeR1(boolean playsafer1) {
		this.playsafer1 = playsafer1;
	}

	public boolean getPlaySafeR2() {
		return playsafer2;
	}

	public void setPlaySafeR2(boolean playsafer2) {
		this.playsafer2 = playsafer2;
	}

	public boolean getPlaySafeR3() {
		return playsafer3;
	}

	public void setPlaySafeR3(boolean playsafer3) {
		this.playsafer3 = playsafer3;
	}

	public String getStretch() {
		return stretch;
	}

	public void setStretch(@NotNull String stretch) {
		this.stretch = stretch;
	}

	public GameNpc getWinningPitcher() {
		return winning_pitcher;
	}

	public void setWinningPitcher(GameNpc winning_pitcher) {
		this.winning_pitcher = winning_pitcher;
	}

	public GameNpc getLosingPitcher() {
		return losing_pitcher;
	}

	public void setLosingPitcher(GameNpc losing_pitcher) {
		this.losing_pitcher = losing_pitcher;
	}

	public GameNpc getSavePitcher() {
		return save_pitcher;
	}

	public void setSavePitcher(GameNpc save_pitcher) {
		this.save_pitcher = save_pitcher;
	}

	public int getSub(boolean isPlayingHome) {
	    if (isPlayingHome) {
	        return getHomePitcherSub();
        } else {
	        return getAwayPitcherSub();
        }
    }

    public void setSub(boolean isPlayingHome, int mark) {
	    if (isPlayingHome) {
	        setHomePitcherSub(getSub(true) & mark);
        } else {
	        setAwayPitcherSub(getSub(false) & mark);
        }
    }

    public int getAiSub(boolean isPlayingHome) {
	    return isPlayingHome ? getHomePitcherSub() : getAwayPitcherSub();
    }

	public int getHomePitcherSub() {
		return homePitcherSub;
	}

	public void setHomePitcherSub(int homePitcherSub) {
		this.homePitcherSub = homePitcherSub;
	}

	public int getAwayPitcherSub() {
		return awayPitcherSub;
	}

	public void setAwayPitcherSub(int awayPitcherSub) {
		this.awayPitcherSub = awayPitcherSub;
	}

	public boolean getGameActive() {
		return gameActive;
	}

	public void setGameActive(boolean game_active) {
		this.gameActive = game_active;
	}

	public String getNextCommand() {
		return next_command;
	}

	public void setNextCommand(String next_command) {
		this.next_command = next_command;
	}

	public boolean getPitch() {
		return pitch;
	}

	public void setPitch(boolean pitch) {
		this.pitch = pitch;
	}

	public int getForfeit() {
		return forfeit;
	}

	public void setForfeit(int forfeit) {
		this.forfeit = forfeit;
	}

	public String getBusy() {
		return busy;
	}

	public void setBusy(@NotNull String busy) {
		this.busy = busy;
	}

	public void setBusy(int busy) {
		this.busy = Integer.toString(busy);
	}

	public int getBusyDelay() {
		if (this.busy_delay < 4) {
			return 4;
		}

		return busy_delay;
	}

	public boolean isFirstBatterOfInning() {
		return firstBatterOfInning;
	}

	public void setFirstBatterOfInning(boolean firstBatterOfInning) {
		this.firstBatterOfInning = firstBatterOfInning;
	}

	public void setBusyDelay(int busy_delay) {
		this.busy_delay = busy_delay;
	}

	public void setBatter(String index, GameNpc npc) {
		setBatter(PHPHelper.toInt(index), npc);
	}

	public void setBatter(int index, GameNpc npc) {
		if (index < 1 || index > 9) {
			throw new IndexOutOfBoundsException();
		}
		switch (index) {
			case 1:
				setBatter1(npc);
				break;
			case 2:
				setBatter2(npc);
				break;
			case 3:
				setBatter3(npc);
				break;
			case 4:
				setBatter4(npc);
				break;
			case 5:
				setBatter5(npc);
				break;
			case 6:
				setBatter6(npc);
				break;
			case 7:
				setBatter7(npc);
				break;
			case 8:
				setBatter8(npc);
				break;
			case 9:
				setBatter9(npc);
				break;
		}
	}

	public GameNpc getBatter(int index) {
		if (index < 1 || index > 9) {
			throw new IndexOutOfBoundsException();
		}
		switch (index) {
			case 1:
				return getBatter1();
			case 2:
				return getBatter2();
			case 3:
				return getBatter3();
			case 4:
				return getBatter4();
			case 5:
				return getBatter5();
			case 6:
				return getBatter6();
			case 7:
				return getBatter7();
			case 8:
				return getBatter8();
			case 9:
				return getBatter9();
		}

		return null;
	}

	public GameNpc getNpcByPositionAndInningPart(String position, boolean inningTop) {
		if (inningTop) {
			switch (position.toLowerCase()) {
				case "pitcher":
					return getHomePitcher();
				case "catcher":
					return getHomeCatcher();
				case "first":
					return getHomeFirst();
				case "second":
					return getHomeSecond();
				case "third":
					return getHomeThird();
				case "shortstop":
				case "short":
					return getHomeShort();
				case "left":
					return getHomeLeft();
				case "center":
					return getHomeCenter();
				case "right":
					return getHomeRight();
				case "hitter":
					return getHomeHitter();
			}
		} else {
			switch (position.toLowerCase()) {
				case "pitcher":
					return getAwayPitcher();
				case "catcher":
					return getAwayCatcher();
				case "first":
					return getAwayFirst();
				case "second":
					return getAwaySecond();
				case "third":
					return getAwayThird();
				case "shortstop":
				case "short":
					return getAwayShort();
				case "left":
					return getAwayLeft();
				case "center":
					return getAwayCenter();
				case "right":
					return getAwayRight();
				case "hitter":
					return getAwayHitter();
			}
		}
		//should not be executed
		return null;
	}

	public void setNpcByPositionAndInningPart(GameNpc npc, String position, boolean isPlayingHome) {
		if (isPlayingHome) {
			switch (position.toLowerCase()) {
				case "pitcher":
					setHomePitcher(npc);
					break;
				case "catcher":
					setHomeCatcher(npc);
					break;
				case "first":
					setHomeFirst(npc);
					break;
				case "second":
					setHomeSecond(npc);
					break;
				case "third":
					setHomeThird(npc);
					break;
				case "shortstop":
				case "short":
					setHomeShort(npc);
					break;
				case "left":
					setHomeLeft(npc);
					break;
				case "center":
					setHomeCenter(npc);
					break;
				case "right":
					setHomeRight(npc);
					break;
				case "hitter":
					setHomeHitter(npc);
					break;

			}
		} else {
			switch (position.toLowerCase()) {
				case "pitcher":
					setAwayPitcher(npc);
					break;
				case "catcher":
					setAwayCatcher(npc);
					break;
				case "first":
					setAwayFirst(npc);
					break;
				case "second":
					setAwaySecond(npc);
					break;
				case "third":
					setAwayThird(npc);
					break;
				case "shortstop":
				case "short":
					setAwayShort(npc);
					break;
				case "left":
					setAwayLeft(npc);
					break;
				case "center":
					setAwayCenter(npc);
					break;
				case "right":
					setAwayRight(npc);
					break;
				case "hitter":
					setAwayHitter(npc);
					break;
			}
		}
	}

	/**
	 * @return true if player has at least one of specified marks.
	 * @see com.lixar.apba.domain.GameReady#REPLACEMENT_UNNECESSARY
	 */
	public boolean hasAnyReplacementMark(boolean playerHome, int... marks) {
		int val = playerHome ? homePitcherSub : awayPitcherSub;
		return hasAnyMark(val, marks);
	}

	private boolean hasAnyMark(int val, int... marks) {
		for (int mark : marks) {
			if ((val & mark) != 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return true if player has at least one injury mark.
	 * @see com.lixar.apba.domain.GameReady#REPLACEMENT_UNNECESSARY
	 */
	public boolean hasInjuryReplacementMark(boolean playerHome) {
		return hasAnyReplacementMark(playerHome, REPLACEMENT_INJURED, REPLACEMENT_INJURED_PITCHER);
	}

	/**
	 * @return true if any player has at least one injury mark.
	 * @see com.lixar.apba.domain.GameReady#REPLACEMENT_UNNECESSARY
	 */
	public boolean hasInjuryReplacementMark() {
		return hasAnyReplacementMark(true, REPLACEMENT_INJURED, REPLACEMENT_INJURED_PITCHER) || hasAnyReplacementMark(false, REPLACEMENT_INJURED, REPLACEMENT_INJURED_PITCHER);
	}

	/**
	 * Adds mark to player. Uses bitwise OR logic - marks are independent.
	 * @see com.lixar.apba.domain.GameReady#REPLACEMENT_UNNECESSARY
	 */
	public void addReplacementMark(boolean playerHome, int mark) {
		if (playerHome) {
			homePitcherSub |= mark;
		} else {
			awayPitcherSub |= mark;
		}
	}

	/**
	 * Deletes mark from player if player has it.
	 * @see com.lixar.apba.domain.GameReady#REPLACEMENT_UNNECESSARY
	 */
	public void deleteReplacementMark(boolean playerHome, int mark) {
		if (playerHome) {
			if (hasAnyReplacementMark(playerHome, mark)) {
				homePitcherSub -= mark;
			}
		} else {
			if (hasAnyReplacementMark(playerHome, mark)) {
				awayPitcherSub -= mark;
			}
		}
	}

	/**
	 * Deletes advice marks from player if player has them.
	 * @see com.lixar.apba.domain.GameReady#REPLACEMENT_UNNECESSARY
	 */
	public void deleteAdviceReplacementMarks(boolean playerHome) {
		deleteReplacementMark(playerHome, REPLACEMENT_ADVICE);
		deleteReplacementMark(playerHome, REPLACEMENT_ADVICE_PITCHER);
	}

	/**
	 * Deletes injury marks from player if player has them.
	 * @see com.lixar.apba.domain.GameReady#REPLACEMENT_UNNECESSARY
	 */
	public void deleteInjuryReplacementMarks(boolean playerHome) {
		deleteReplacementMark(playerHome, REPLACEMENT_INJURED);
		deleteReplacementMark(playerHome, REPLACEMENT_INJURED_PITCHER);
	}

	public List<GameNpc> getOffensePlayers() {
		List<GameNpc> players = new ArrayList<>();
		for (int i = 1; i <= 9; i++) {
			players.add(getBatter(i));
		}
		return players;
	}

	public List<GameNpc> getDefensePlayers() {
		List<GameNpc> players = new ArrayList<>();
		if (inningTop) {
			players.add(homePitcher);
			players.add(homeCatcher);
			players.add(homeFirst);
			players.add(homeSecond);
			players.add(homeThird);
			players.add(homeShort);
			players.add(homeLeft);
			players.add(homeCenter);
			players.add(homeRight);
		} else {
			players.add(awayPitcher);
			players.add(awayCatcher);
			players.add(awayFirst);
			players.add(awaySecond);
			players.add(awayThird);
			players.add(awayShort);
			players.add(awayLeft);
			players.add(awayCenter);
			players.add(awayRight);
		}
		return players;
	}

    public Integer getAwayMicroManagerId() {
        return awayMicroManagerId;
    }

    public void setAwayMicroManagerId(Integer awayMicroManagerId) {
        this.awayMicroManagerId = awayMicroManagerId;
    }

    public Integer getHomeMicroManagerId() {
        return homeMicroManagerId;
    }

    public void setHomeMicroManagerId(Integer homeMicroManagerId) {
        this.homeMicroManagerId = homeMicroManagerId;
    }

    public String getHomeTeamYear() {
		return homeTeamYear;
	}

	public void setHomeTeamYear(String homeTeamYear) {
		this.homeTeamYear = homeTeamYear;
	}

	public String getAwayTeamYear() {
		return awayTeamYear;
	}

	public void setAwayTeamYear(String awayTeamYear) {
		this.awayTeamYear = awayTeamYear;
	}

    /**
     * Determines whether the player is scheduled to bat next inning.
     * @return true if the player is one of the next 3 scheduled batters.
     */
    public boolean playerIsDueUp(boolean isPlayingHome, String playerBattingOrder) {
        int currentBattingOrder = getBattingHome();
        if (!isPlayingHome) {
            currentBattingOrder = getBattingAway();
        }
        if (currentBattingOrder <= 6) {
            if (PHPHelper.toInt(playerBattingOrder) >= currentBattingOrder && PHPHelper.toInt(playerBattingOrder) <= currentBattingOrder + 3) {
                return true;
            }
        } else {
            int playerBattingOrderAdjusted = PHPHelper.toInt(playerBattingOrder) + (9 - currentBattingOrder);
            if (currentBattingOrder >= playerBattingOrderAdjusted && playerBattingOrderAdjusted <= currentBattingOrder + 3) {
                return true;
            }
        }
        return false;
    }

	public boolean isBeginningOfInning() {
		return beginningOfInning;
	}

	public void setBeginningOfInning(boolean beginningOfInning) {
		this.beginningOfInning = beginningOfInning;
	}
	
	/**
	 * Sets value according to the team side
	 * @param score 
	 * @param teamSide
	 */
	public void setScore(Integer score, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHome(score);
		} else {
			setAway(score);
		}
	}

	/**
	 * Sets value according to the team side
	 * @param player
	 * @param teamSide
	 */
	public void setPlayer(GamePlayer player, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomePlayer(player);
		} else {
			setAwayPlayer(player);
		}
	}

	/**
	 * Sets value according to the team side
	 * @param lineup
	 * @param teamSide
	 */
	public void setLineup(Boolean lineup, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeLineup(lineup);
		} else {
			setAwayLineup(lineup);
		}
	}

	/**
	 * Sets value according to the team side
	 * @param npc
	 * @param teamSide
	 */
	public void setPitcher(GameNpc npc, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomePitcher(npc);
		} else {
			setAwayPitcher(npc);
		}
	}

	/**
	 * Sets value according to the team side
	 * @param npc
	 * @param teamSide
	 */
	public void setCatcher(GameNpc npc, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeCatcher(npc);
		} else {
			setAwayCatcher(npc);
		}
	}

	/**
	 * Sets value according to the team side
	 * @param npc
	 * @param teamSide
	 */
	public void setFirst(GameNpc npc, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeFirst(npc);
		} else {
			setAwayFirst(npc);
		}
	}

	/**
	 * Sets value according to the team side
	 * @param npc
	 * @param teamSide
	 */
	public void setSecond(GameNpc npc, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeSecond(npc);
		} else {
			setAwaySecond(npc);
		}
	}

	/**
	 * Sets value according to the team side
	 * @param npc
	 * @param teamSide
	 */
	public void setThird(GameNpc npc, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeThird(npc);
		} else {
			setAwayThird(npc);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param npc
	 * @param teamSide
	 */
	public void setShort(GameNpc npc, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeShort(npc);
		} else {
			setAwayShort(npc);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param npc
	 * @param teamSide
	 */
	public void setLeft(GameNpc npc, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeLeft(npc);
		} else {
			setAwayLeft(npc);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param npc
	 * @param teamSide
	 */
	public void setCenter(GameNpc npc, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeCenter(npc);
		} else {
			setAwayCenter(npc);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param npc
	 * @param teamSide
	 */
	public void setRight(GameNpc npc, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeRight(npc);
		} else {
			setAwayRight(npc);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param npc
	 * @param teamSide
	 */
	public void setHitter(GameNpc npc, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeHitter(npc);
		} else {
			setAwayHitter(npc);
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public Integer getScore(Side teamSide) {
		if (teamSide == Side.HOME) {
			return getHome();
		} else {
			return getAway();
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public GamePlayer getPlayer(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homePlayer;
		} else {
			return this.awayPlayer;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public boolean getLineup(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homeLineup;
		} else {
			return this.awayLineup;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public GameNpc getPitcher(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homePitcher;
		} else {
			return this.awayPitcher;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public GameNpc getCatcher(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homeCatcher;
		} else {
			return this.awayCatcher;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public GameNpc getFirst(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homeFirst;
		} else {
			return this.awayFirst;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public GameNpc getSecond(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homeSecond;
		} else {
			return this.awaySecond;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public GameNpc getThird(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homeThird;
		} else {
			return this.awayThird;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public GameNpc getShort(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homeShort;
		} else {
			return this.awayShort;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public GameNpc getLeft(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homeLeft;
		} else {
			return this.awayLeft;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public GameNpc getCenter(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homeCenter;
		} else {
			return this.awayCenter;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public GameNpc getRight(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homeRight;
		} else {
			return this.awayRight;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 */
	public GameNpc getHitter(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homeHitter;
		} else {
			return this.awayHitter;
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param year
	 * @param teamSide
	 */
	public void setTeamYear(String year, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeTeamYear(year);
		} else {
			setAwayTeamYear(year);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param id
	 * @param teamSide
	 */
	public void setMicroManagerId(Integer id, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeMicroManagerId(id);
		} else {
			setAwayMicroManagerId(id);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param pSub - "pitcher sub"
	 * @param teamSide
	 */
	public void setPitcherSub(int pSub, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomePitcherSub(pSub);
		} else {
			setAwayPitcherSub(pSub);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param abbr
	 * @param teamSide
	 */
	public void setAbbr(String abbr, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeAbbr(abbr);
		} else {
			setAwayAbbr(abbr);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param team
	 * @param teamSide
	 */
	public void setTeam(String team, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeTeam(team);
		} else {
			setAwayTeam(team);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param batting
	 * @param teamSide
	 */
	public void setBatting(Integer batting, Side teamSide) {
		if (teamSide == Side.HOME) {
			setBattingHome(batting);
		} else {
			setBattingAway(batting);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param hits
	 * @param teamSide
	 */
	public void setHits(int hits, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeHits(hits);
		} else {
			setAwayHits(hits);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param errors
	 * @param teamSide
	 */
	public void setErrors(int errors, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeErrors(errors);
		} else {
			setAwayErrors(errors);
		}
	}
	
	/**
	 * Sets value according to the team side
	 * @param score - Per inning, enclosed in a string
	 * @param teamSide
	 */
	public void setInningscore(String score, Side teamSide) {
		if (teamSide == Side.HOME) {
			setHomeInningscore(score);
		} else {
			setAwayInningscore(score);
		}
	}
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away Team Year
	 */
	public String getTeamYear(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homeTeamYear;
		} else {
			return this.awayTeamYear;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away MicroManager Id
	 */
	public Integer getMicroManagerId(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homeMicroManagerId;
		} else {
			return this.awayMicroManagerId;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away Pitcher-sub flag
	 */
	public int getPitcherSub(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.homePitcherSub;
		} else {
			return this.awayPitcherSub;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away Team abbreviation
	 */
	public String getAbbr(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.home_abbr;
		} else {
			return this.away_abbr;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away Team
	 */
	public String getTeam(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.home_team;
		} else {
			return this.away_team;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away Batting position
	 */
	public Integer getBatting(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.batting_home;
		} else {
			return this.batting_away;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away hit count
	 */
	public int getHits(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.home_hits;
		} else {
			return this.away_hits;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away error count
	 */
	public int getErrors(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.home_errors;
		} else {
			return this.away_errors;
		}
	}
	
	/**
	 * Gets value according to the team side
	 * @param teamSide
	 * @return Desired home or away inningscore
	 */
	public String getInningscore(Side teamSide) {
		if (teamSide == Side.HOME) {
			return this.home_inningscore;
		} else {
			return this.away_inningscore;
		}
	}
}
