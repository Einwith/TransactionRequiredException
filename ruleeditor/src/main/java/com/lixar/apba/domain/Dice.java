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
@Table(name = "dice")
public class Dice implements IdAble {

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
	@JoinColumn(name="client")
    private Client client;

    @Column(name="sid")
	private Integer sid;

    @Column(name="pool")
	private Integer pool;

    @Column(name="name")
	private String name;

    @Column(name="dice")
	private Integer dice;

    @Column(name="count")
	private Integer count;

    @Column(name="rule")
	private String rule;

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

	public Integer getSid() {
		return sid;
	}

	public void setSid(Integer sid) {
		this.sid = sid;
	}

	public Integer getPool() {
		return pool;
	}

	public void setPool(Integer pool) {
		this.pool = pool;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDice() {
		return dice;
	}

	public void setDice(Integer dice) {
		this.dice = dice;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
       	
}
