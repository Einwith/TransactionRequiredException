package com.lixar.apba.core.sql.mapper;

import com.lixar.apba.core.sql.MySQLStringBuilder;
import com.lixar.apba.core.sql.SQLInsertMap;
import com.lixar.apba.domain.BookEntryHistory;

public class BookEntryHistorySQLMapper implements SQLInsertMap<BookEntryHistory> {
    @Override
    public String toInsertSQLString(BookEntryHistory history) {
        MySQLStringBuilder builder = new MySQLStringBuilder();

        if (history != null) {
            builder.appendRaw("INSERT INTO `book_entry_history` (`id`,`result_id`,`result_time`,`result_old`,`condition_old`,`action_old`) VALUES (");
            builder.appendValue(history.getId());
            builder.appendValue(history.getResultId());
            builder.appendValue(history.getResultTime());
            builder.appendValue(history.getResultOld());
            builder.appendValue(history.getConditionOld());
            builder.appendValue(history.getActionOld(), false);
            builder.appendRaw(");");
        }

        return builder.toString();
    }
}
