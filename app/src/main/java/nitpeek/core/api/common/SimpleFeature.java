package nitpeek.core.api.common;

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

    /**
     * @return an unmodifiable copy
     */
    @Override
    public List<FeatureComponent> getComponents() {
        return components;
    }
}
