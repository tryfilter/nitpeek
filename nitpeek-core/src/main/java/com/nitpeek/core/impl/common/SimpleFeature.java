package com.nitpeek.core.impl.common;

import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.FeatureComponent;
import com.nitpeek.core.api.common.FeatureType;

import java.util.List;
import java.util.Objects;

public record SimpleFeature(FeatureType type, List<FeatureComponent> components, double confidence) implements Feature {
    public SimpleFeature {
        if (confidence < 0 || confidence > 1)
            throw new IllegalArgumentException("confidence must be in the interval [0, 1] but was " + confidence);
        Objects.requireNonNull(type);
        Objects.requireNonNull(components);
        components = List.copyOf(components);
    }

    @Override
    public double getConfidence() {
        return confidence;
    }

    @Override
    public FeatureType getType() {
        return type;
    }

    @Override
    public List<FeatureComponent> getComponents() {
        return components;
    }
}
