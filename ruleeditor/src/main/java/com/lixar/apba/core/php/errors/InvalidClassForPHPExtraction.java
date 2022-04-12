package com.lixar.apba.core.php.errors;

public class InvalidClassForPHPExtraction extends Exception {
    public InvalidClassForPHPExtraction() {
        super();
    }

    public InvalidClassForPHPExtraction(String message) {
        super(message);
    }

    public InvalidClassForPHPExtraction(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidClassForPHPExtraction(Throwable cause) {
        super(cause);
    }

    protected InvalidClassForPHPExtraction(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
