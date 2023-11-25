package nitpeek.core.api.common;

public record ProblemType(String name, Severity severity, String description) {
    public enum Severity {
        MIN,
        LOW,
        MEDIUM,
        HIGH,
        MAX
    }
}
