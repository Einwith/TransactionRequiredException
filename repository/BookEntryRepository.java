package com.lixar.apba.repository;

import com.lixar.apba.domain.BookEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookEntryRepository extends JpaRepository<BookEntry, Integer> {

	/**
     * Finds the book entry by page/section (otherwise known as dice_pool/dice_sid
     *
     * @param dicePool aka the page
     * @param diceSid aka the section
     * @param requirement aka requirement
     * @return the found book entries
     */
    List<BookEntry> findAllByPageAndSectionAndResultId(int dicePool, int diceSid, String requirement);
    
    List<BookEntry> findAllByPageAndSectionAndResultIdAndResultBook(int dicePool, int diceSid, String resultId, String resultBook);
}
