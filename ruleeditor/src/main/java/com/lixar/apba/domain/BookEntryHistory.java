package com.lixar.apba.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "book_entry_history")
public class BookEntryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "result_id")
    private int resultId;
    
    @Column(name = "result_time")
    private long resultTime;
    
    @Column(name = "result_old")
    private String resultOld;
    
    @Column(name = "condition_old")
    private String conditionOld;

    @Column(name = "action_old")
    private String actionOld;
    
    public BookEntryHistory(){}
    
    public BookEntryHistory(BookEntry bookEntry) {
    	//Note: book_entry_history table has a foreign key
    	//on column result_id which points to id column
    	//of book_entry. 
    	this.resultId = bookEntry.getId();
    	this.resultOld = bookEntry.getResultCurrent();
    	this.conditionOld = bookEntry.getParserCondition();
    }
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getResultId() {
		return resultId;
	}

	public void setResultId(int resultId) {
		this.resultId = resultId;
	}

	public long getResultTime() {
		return resultTime;
	}

	public void setResultTime(long resultTime) {
		this.resultTime = resultTime;
	}

	public String getResultOld() {
		return resultOld;
	}

	public void setResultOld(String resultOld) {
		this.resultOld = resultOld;
	}

	public String getConditionOld() {
		return conditionOld;
	}

	public void setConditionOld(String conditionOld) {
		this.conditionOld = conditionOld;
	}

	public String getActionOld() {
		return actionOld;
	}

	public void setActionOld(String actionOld) {
		this.actionOld = actionOld;
	}
}
