package com.lixar.apba.core.sql.mapper;

import com.lixar.apba.core.sql.MySQLStringBuilder;
import com.lixar.apba.core.sql.SQLInsertMap;
import com.lixar.apba.domain.BookPushHistory;

public class BookPushHistorySQLMapper implements SQLInsertMap<BookPushHistory> {
    @Override
    public String toInsertSQLString(BookPushHistory history) {
        MySQLStringBuilder builder = new MySQLStringBuilder();

        if (history != null) {
            builder.appendRaw("INSERT INTO `book_push_history` (`id`,`user_id`,`push_time`) VALUES (");
            builder.appendValue(history.getId());
            builder.appendValue(history.getUserId());
            builder.appendValue(history.getPushTime(), false);
            builder.appendRaw(");");
        }

        return builder.toString();
    }
}
