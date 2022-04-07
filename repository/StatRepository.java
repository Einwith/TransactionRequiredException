package com.lixar.apba.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lixar.apba.domain.Stat;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;

public interface StatRepository extends JpaRepository<Stat, Integer>{
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value ="true") })
	Stat findOneByReference(String reference);
}
