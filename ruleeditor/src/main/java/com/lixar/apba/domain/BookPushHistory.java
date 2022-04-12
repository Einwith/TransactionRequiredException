package com.lixar.apba.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "book_push_history")
public class BookPushHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name="user_id")
    private int userId;
    
    @Column(name="push_time")
    private long pushTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public long getPushTime() {
		return pushTime;
	}

	public void setPushTime(long pushTime) {
		this.pushTime = pushTime;
	}
    
    
}
