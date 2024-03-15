package nitpeek.core.impl.common;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.FeatureType;

import java.util.List;

/**
 * Convenience implementation.
 */
public final class AnonymousFeature implements Feature {

    private final List<FeatureComponent> featureComponents;

    public AnonymousFeature(List<FeatureComponent> featureComponents) {
        this.featureComponents = List.copyOf(featureComponents);
    }

    @Override
    public double getConfidence() {
        return 0;
    }

    @Override
    public FeatureType getType() {
        return new SimpleFeatureType("nitpeek.feature.anonymous",
                "Anonymous Feature",
                FeatureType.Classification.INFO,
                "This is an anonymous feature, only intended for implementation details."
        );
    }

    @Override
    public List<FeatureComponent> getComponents() {
        return featureComponents;
    }
}
