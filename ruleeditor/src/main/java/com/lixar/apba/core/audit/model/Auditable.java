package com.lixar.apba.core.audit.model;

import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

	@CreatedBy
	@Column(name = "created_by", length = 50)
	private String createdBy;

	@CreatedDate
	@NotAudited // Currently there is a support issue under 4.x of Hibernate/Envers for LocalDateTime,
	            // Can be supported once moved to hibernate 5 with hibernate-java8 dependency
	@Column(name = "created_date")
	private LocalDateTime createdDate = LocalDateTime.now();

	@LastModifiedBy
	@Column(name = "last_modified_by", length = 50)
	private String lastModifiedBy;

	@LastModifiedDate
	@NotAudited // Currently there is a support issue under 4.x of Hibernate/Envers for LocalDateTime,
	            // Can be supported once moved to hibernate 5 with hibernate-java8 dependency
	@Column(name = "last_modified_date")
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}