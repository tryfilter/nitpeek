package nitpeek.core.api.common;

public enum StandardFeature {

    MISSING_PAGES(new FeatureType("Missing Pages", FeatureType.Classification.WARNING, "Fewer pages were processed than expected."));
    private final FeatureType type;

    StandardFeature(FeatureType type) {
        this.type = type;
    }

    public FeatureType getType() {
        return type;
    }
}
