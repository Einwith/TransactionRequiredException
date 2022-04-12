package com.lixar.apba.core.sql.mapper;

import com.lixar.apba.core.sql.MySQLStringBuilder;
import com.lixar.apba.core.sql.SQLInsertMap;
import com.lixar.apba.domain.BookEntry;

public class BookEntrySQLMapper implements SQLInsertMap<BookEntry> {
    @Override
    public String toInsertSQLString(BookEntry book) {
        MySQLStringBuilder builder = new MySQLStringBuilder();

        if (book != null) {
            builder.appendRaw("INSERT INTO `book_entry` (`id`,`client`,`page`,`section`,`result_id`,`result_book`,`result_current`,`local`,`text_condition`) VALUES (");
            builder.appendValue(book.getId());
            builder.appendValue(book.getClient());
            builder.appendValue(book.getPage());
            builder.appendValue(book.getSection());
            builder.appendValue(book.getResultId());
            builder.appendValue(book.getResultBook());
            builder.appendValue(book.getResultCurrent());
            builder.appendValue(book.getLocal());
            builder.appendValue(book.getTextCondition(), false);
            builder.appendRaw(");");
        }

        return builder.toString();
    }
}
