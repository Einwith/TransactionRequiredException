package com.lixar.apba.domain;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "system_node")
@DynamicUpdate
public class SystemNode {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO,
    		generator = "native"
    )
    @GenericGenerator(
    		name = "native",
    		strategy = "native"
    )
	@Column(name = "id")
	private Integer id;

	@Column(name = "time_stamp")
	private String timestamp;

	/** The ip. */
	@Column(name = "ip")
	private String ip;

	/** The last ping. */
	@Column(name = "last_ping", nullable = false, columnDefinition="timestamp with time zone")
    @Temporal(TemporalType.TIMESTAMP)
	private Date lastPing;

	@Column(name = "created_at", nullable = false, columnDefinition="timestamp with time zone")
    @Temporal(TemporalType.TIMESTAMP)
	private Date createdAt = new Date();

	@Column(name = "is_leader")
	private Boolean isLeader = Boolean.FALSE;

	public Integer getId() {
	    return id;
	}

	public void setId(final Integer id) {
	    this.id = id;
	}

	public String getTimestamp() {
	    return timestamp;
	}

	public void setTimestamp(final String timestamp) {
	    this.timestamp = timestamp;
	}

	public String getIp() {
	    return ip;
	}

	public void setIp(final String ip) {
	    this.ip = ip;
	}

	public Date getLastPing() {
	    return lastPing;
	}

	public void setLastPing(final Date lastPing) {
	    this.lastPing = lastPing;
	}

	public Date getCreatedAt() {
	    return createdAt;
	}

	public void setCreatedAt(final Date createdAt) {
	    this.createdAt = createdAt;
	}

	public Boolean getIsLeader() {
	    return isLeader;
	}

	public void setIsLeader(final Boolean isLeader) {
	    this.isLeader = isLeader;
	}

	@Override
	public String toString() {
	    return "SystemNode{" +
	            "id=" + id +
	            ", timestamp='" + timestamp + '\'' +
	            ", ip='" + ip + '\'' +
	            ", lastPing=" + lastPing +
	            ", createdAt=" + createdAt +
	            ", isLeader=" + isLeader +
	            '}';
	}

}
