package com.lixar.apba.core.sql.errors;

public class InvalidClassForSQLExtraction extends Exception {
    public InvalidClassForSQLExtraction() {
        super();
    }

    public InvalidClassForSQLExtraction(String message) {
        super(message);
    }

    public InvalidClassForSQLExtraction(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidClassForSQLExtraction(Throwable cause) {
        super(cause);
    }

    protected InvalidClassForSQLExtraction(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
