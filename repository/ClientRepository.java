package com.lixar.apba.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lixar.apba.domain.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {

	Client findOneByName(String name);
}
