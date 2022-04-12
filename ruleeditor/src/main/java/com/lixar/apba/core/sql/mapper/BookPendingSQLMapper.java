package com.lixar.apba.core.sql.mapper;

import com.lixar.apba.core.sql.MySQLStringBuilder;
import com.lixar.apba.core.sql.SQLInsertMap;
import com.lixar.apba.domain.BookPending;

public class BookPendingSQLMapper implements SQLInsertMap<BookPending> {
    @Override
    public String toInsertSQLString(BookPending pending) {
        MySQLStringBuilder builder = new MySQLStringBuilder();

        if (pending != null) {
            builder.appendRaw("INSERT INTO `book_pending` (`id`,`client`,`page`,`section`,`result_id`,`result_book`,`result_current`,`local`,`creator`,`text_condition`,`condition_order`,`action_outcome`,`deletion_pending`) VALUES (");
            builder.appendValue(pending.getId());
            builder.appendValue(pending.getClient());
            builder.appendValue(pending.getPage());
            builder.appendValue(pending.getSection());
            builder.appendValue(pending.getResultId());
            builder.appendValue(pending.getResultBook());
            builder.appendValue(pending.getResultCurrent());
            builder.appendValue(pending.getLocal());
            builder.appendValue(pending.getCreator());
            builder.appendValue(pending.getTextCondition());
            builder.appendValue(pending.getConditionOrder());
            builder.appendValue(pending.getActionOutcome());
            builder.appendValue(pending.getDeletionPending(), false);
            builder.appendRaw(");");
        }

        return builder.toString();
    }
}
