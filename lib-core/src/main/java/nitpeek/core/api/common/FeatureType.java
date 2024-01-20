package nitpeek.core.api.common;


public interface FeatureType {
    Identifier getFeatureId();

    Classification getClassification();

    enum Classification {
        INFO,
        WARNING,
        ERROR
    }
}
