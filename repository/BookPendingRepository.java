package com.lixar.apba.repository;

import com.lixar.apba.domain.BookPending;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookPendingRepository extends JpaRepository<BookPending, Integer> {

    @Query("select bp from BookPending bp order by bp.page, bp.section, bp.resultId, bp.conditionOrder asc")
    List<BookPending> findAllOrderByPageSectionResult_IdCondition_OrderASC();
}
