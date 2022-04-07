package com.lixar.apba.repository;


import com.lixar.apba.domain.SystemNode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemNodeRepository extends JpaRepository<SystemNode, Integer> {
    SystemNode findByIp(String ip);
}
