package com.lixar.apba.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lixar.apba.domain.BookEntryHistory;

public interface BookEntryHistoryRepository extends JpaRepository<BookEntryHistory, Integer>{

}
