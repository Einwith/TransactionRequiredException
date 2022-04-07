package com.lixar.apba.service.book.converter;

import com.lixar.apba.domain.BookEntry;
import com.lixar.apba.domain.BookPending;
import org.springframework.stereotype.Service;

@Service
public class BookEntryFactory {

    public BookEntry createBookEntry(BookPending bookPending) {
        BookEntry bookEntry = new BookEntry();
        bookEntry.setClient(bookPending.getClient());
        bookEntry.setPage(bookPending.getPage());
        bookEntry.setSection(bookPending.getSection());
        bookEntry.setResultId(bookPending.getResultId());
        bookEntry.setResultBook(bookPending.getParserCondition());
        bookEntry.setResultCurrent(bookPending.getResultCurrent());
        bookEntry.setLocal(bookPending.getLocal());
        bookEntry.setTextCondition(bookPending.getParserCondition());

        return bookEntry;
    }

	public BookPending createBookPending(BookEntry bookEntry) {
        BookPending newBookPending = new BookPending();
        newBookPending.setClient(bookEntry.getClient());
        newBookPending.setPage(bookEntry.getPage());
        newBookPending.setSection(bookEntry.getSection());
        newBookPending.setResultId(bookEntry.getResultId());
        newBookPending.setResultBook(bookEntry.getResultBook());
        newBookPending.setResultCurrent(bookEntry.getResultCurrent());
        newBookPending.setLocal(bookEntry.getLocal());
        newBookPending.setTextCondition(bookEntry.getTextCondition());

        return newBookPending;
    }
}
