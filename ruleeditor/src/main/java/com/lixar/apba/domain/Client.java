package com.lixar.apba.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


@Entity
@Table(name = "Client")
public class Client implements IdAble, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,
    		generator = "native"
    )
    @GenericGenerator(
    		name = "native",
    		strategy = "native"
    )
    private Integer id;
    
    @Column(name="name")
    private String name;

    @Column(name="version")
    private String version;

    @Column(name="board")
    private boolean board;

    public Client() {
    	
    }
    
    public Client(int id, String name, String version, boolean board) {
    	this.id = id;
    	this.name = name;
    	this.version = version;
    	this.board = board;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isBoard() {
		return board;
	}

	public void setBoard(boolean board) {
		this.board = board;
	}
}
