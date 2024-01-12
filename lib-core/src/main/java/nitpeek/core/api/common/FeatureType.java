package nitpeek.core.api.common;

public record FeatureType(String id, String name, Classification classification, String description) {
    public enum Classification {
        INFO,
        WARNING,
        ERROR
    }
}
