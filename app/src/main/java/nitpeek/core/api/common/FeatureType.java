package nitpeek.core.api.common;

public record FeatureType(String name, Classification classification, String description) {
    public enum Classification {
        INFO,
        WARNING,
        ERROR
    }
}
