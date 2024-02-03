package nitpeek.core.impl.common;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.FeatureType;
import nitpeek.core.api.common.TextSelection;

import java.util.List;

/**
 * Convenience implementation.
 */
public final class AnonymousSingleComponentFeature implements Feature {

    private final Feature feature;

    public AnonymousSingleComponentFeature(TextSelection coordinates) {
        this(null, coordinates);
    }

    public AnonymousSingleComponentFeature(String relevantTextPortion, TextSelection coordinates) {
        this(relevantTextPortion,
                coordinates,
                "This is an anonymous feature component, only intended for implementation details."
        );
    }

    public AnonymousSingleComponentFeature(String relevantTextPortion, TextSelection coordinates, String description) {
        this(new AnonymousFeatureComponent(relevantTextPortion, coordinates, description));
    }

    public AnonymousSingleComponentFeature(FeatureComponent component) {
        feature = new AnonymousFeature(List.of(component));
    }

    @Override
    public double getConfidence() {
        return feature.getConfidence();
    }

    @Override
    public FeatureType getType() {
        return feature.getType();
    }

    /**
     * @return an unmodifiable copy
     */
    @Override
    public List<FeatureComponent> getComponents() {
        return feature.getComponents();
    }
}
