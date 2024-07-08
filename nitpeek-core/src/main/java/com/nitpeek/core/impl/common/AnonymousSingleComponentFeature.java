package com.nitpeek.core.impl.common;

import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.FeatureComponent;
import com.nitpeek.core.api.common.FeatureType;
import com.nitpeek.core.api.common.TextSelection;

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

    @Override
    public List<FeatureComponent> getComponents() {
        return feature.getComponents();
    }
}
