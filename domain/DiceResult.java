package com.lixar.apba.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "dice_result")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DiceResult implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,
    		generator = "native"
    )
    @GenericGenerator(
    		name = "native",
    		strategy = "native"
    )
	private Integer id;

	@Column(name = "client")
	private Integer client;

	@Column(name = "dice")
	private Integer dice;

	@Column(name = "dice_sid")
	private int diceSid;

	@Column(name = "dice_pool")
	private int dicePool;

	@Column(name = "requirement", length = 255)
	private String requirement;

	@Column(name = "result_parser")
	private String resultParser;

	@Column(name = "result_console")
	private String resultConsole;

	@Column(name = "result_human")
	private String resultHuman;

	@Column(name = "result_text")
	private String resultText;

	@Column(name = "trigger_type", length = 255)
	private String triggerType;

	@Column(name = "trigger_id", length = 255)
	private String trigger_id;

	@Column(name = "trigger_sid")
	private Integer triggerSid;

	@Column(name = "trigger_pool")
	private Integer triggerPool;

	@Column(name = "trigger_end")
	private boolean triggerEnd;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getClient() {
		return client;
	}

	public void setClient(Integer client) {
		this.client = client;
	}

	public Integer getDice() {
		return dice;
	}

	public void setDice(Integer dice) {
		this.dice = dice;
	}

	public int getDiceSid() {
		return diceSid;
	}

	public void setDiceSid(int diceSid) {
		this.diceSid = diceSid;
	}

	public int getDicePool() {
		return dicePool;
	}

	public void setDicePool(int dicePool) {
		this.dicePool = dicePool;
	}

	public String getRequirement() {
		return requirement;
	}

	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}

	public String getResultParser() {
		return resultParser;
	}

	public void setResultParser(String resultParser) {
		this.resultParser = resultParser;
	}

	public String getResultConsole() {
		return resultConsole;
	}

	public void setResultConsole(String resultConsole) {
		this.resultConsole = resultConsole;
	}

	public String getResultHuman() {
		return resultHuman;
	}

	public void setResultHuman(String resultHuman) {
		this.resultHuman = resultHuman;
	}

	public String getResultText() {
		return resultText;
	}

	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

	public String getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	public String getTriggerId() {
		return trigger_id;
	}

	public void setTriggerId(String trigger_id) {
		this.trigger_id = trigger_id;
	}

	public Integer getTriggerSid() {
		return triggerSid;
	}

	public void setTriggerSid(Integer triggerSid) {
		this.triggerSid = triggerSid;
	}

	public Integer getTriggerPool() {
		return triggerPool;
	}

	public void setTriggerPool(Integer triggerPool) {
		this.triggerPool = triggerPool;
	}

	public boolean getTriggerEnd() {
		return triggerEnd;
	}

	public void setTriggerEnd(boolean triggerEnd) {
		this.triggerEnd = triggerEnd;
	}
}
