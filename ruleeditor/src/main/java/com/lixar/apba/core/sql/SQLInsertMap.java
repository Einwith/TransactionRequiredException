package com.lixar.apba.core.sql;

import com.lixar.apba.core.sql.errors.InvalidClassForSQLExtraction;

public interface SQLInsertMap<K> {
    String toInsertSQLString(K object) throws InvalidClassForSQLExtraction;
}
