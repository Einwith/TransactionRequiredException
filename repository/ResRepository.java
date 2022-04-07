package com.lixar.apba.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lixar.apba.domain.Res;

public interface ResRepository extends JpaRepository<Res, Integer>{

	Res findOneByReference(String reference);
}
