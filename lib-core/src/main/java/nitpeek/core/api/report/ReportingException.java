package nitpeek.core.api.report;

/**
 * This exception type represents an error that occurred during a reporting operation
 */
public final class ReportingException extends Exception {

    ReportingException() {
    }

    ReportingException(String message) {
        super(message);
    }

    ReportingException(String message, Throwable cause) {
        super(message, cause);
    }

    ReportingException(Throwable cause) {
        super(cause);
    }

    ReportingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
