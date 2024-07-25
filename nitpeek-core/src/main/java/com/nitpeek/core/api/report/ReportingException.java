package com.nitpeek.core.api.report;

/**
 * This exception type represents an error that occurred during a reporting operation
 */
public final class ReportingException extends Exception {

    public enum Problem {
        UNSPECIFIED,
        INPUT,
        PROCESSING,
        OUTPUT
    }

    private final Problem problemType;

    public ReportingException(Problem problemType) {
        this.problemType = problemType;
    }

    public ReportingException(String message, Problem problemType) {
        super(message);
        this.problemType = problemType;
    }

    public ReportingException(String message, Throwable cause, Problem problemType) {
        super(message, cause);
        this.problemType = problemType;
    }

    public ReportingException(Throwable cause, Problem problemType) {
        super(cause);
        this.problemType = problemType;
    }

    public ReportingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Problem problemType) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.problemType = problemType;
    }

    public Problem problemType() {
        return problemType;
    }
}
