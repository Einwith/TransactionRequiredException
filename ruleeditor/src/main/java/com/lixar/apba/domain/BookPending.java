package com.lixar.apba.domain;

import com.lixar.apba.core.audit.model.Auditable;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "book_pending")
@Audited(auditParents = Auditable.class)
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BookPending extends Auditable implements IBookEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="client")
    private Integer client;

    /** refers to the dice_pool */
    @Column(name="page")
    private int page;

    /** refers to the dice_sid */
    @Column(name="section")
    private int section;

    /** refers to the requirement (aka dice combination) */
    @Column(name="result_id")
    private String resultId;

    /** refers to the original condition  - but note that the text_condition is the final condition*/
    @Column(name="result_book", length=250)
    private String resultBook;

    @Column(name="result_current", length=250)
    private String resultCurrent;

    @Column(name="local", length=5)
    private String local;

    @Column(name="text_condition", length=250)
    private String textCondition;

    @Column(name="creator", length=40)
    private String creator;

    @Column(name="condition_order")
    private Integer conditionOrder;

    @Column(name="action_outcome")
    private String actionOutcome;

    @Column(name="deletion_pending")
    private boolean deletionPending;

    @Column(name = "rulebook_text")
    private String rulebookText;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getClient() {
        return client;
    }

    public void setClient(Integer client) {
        this.client = client;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getResultBook() {
        return resultBook;
    }

    public void setResultBook(String resultBook) {
        this.resultBook = resultBook;
    }

    public String getResultCurrent() {
        return resultCurrent;
    }

    public void setResultCurrent(String resultCurrent) {
        this.resultCurrent = resultCurrent;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getTextCondition() {
        return textCondition;
    }

    public void setTextCondition(String textCondition) {
        this.textCondition = textCondition;
    }

	/**
	 * Deals with a current inconsistency in the model - chooses between text condition and result book
     *
     * @return
     */
    public String getParserCondition() {
        if (StringUtils.isNotBlank(textCondition) && !textCondition.equals(DEFAULT_CONDITION)) {
            return textCondition;
        }

        return resultBook;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

	public Integer getConditionOrder() {
		return conditionOrder;
	}

	public void setConditionOrder(Integer conditionOrder) {
		this.conditionOrder = conditionOrder;
	}

	public String getActionOutcome() {
		return actionOutcome;
	}

	public void setActionOutcome(String acition_outcome) {
		this.actionOutcome = acition_outcome;
	}

	public boolean getDeletionPending() {
		return deletionPending;
	}

	public void setDeletionPending(boolean deletionPending) {
		this.deletionPending = deletionPending;
	}

    public String getRulebookText() {
        return rulebookText;
    }

    public void setRulebookText(String rulebookText) {
        this.rulebookText = rulebookText;
    }
}


