package com.nitpeek.core.api.report;

/**
 * This exception type represents an error that occurred during a reporting operation
 */
public final class ReportingException extends Exception {

    public ReportingException() {
    }

    public ReportingException(String message) {
        super(message);
    }

    public ReportingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReportingException(Throwable cause) {
        super(cause);
    }

    public ReportingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
