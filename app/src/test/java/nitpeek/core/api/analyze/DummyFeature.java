package nitpeek.core.api.analyze;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.FeatureType;

import java.util.List;

/**
 * Placeholder feature to use for tests
 */
public final class DummyFeature implements Feature {

    private final String name;

    public DummyFeature(String name) {
        this.name = name;
    }

    @Override
    public double getConfidence() {
        return 0;
    }

    @Override
    public FeatureType getType() {
        return new FeatureType(
                "dummy-feature",
                name,
                FeatureType.Classification.INFO,
                "This is a dummy feature implementation for testing purposes"
        );
    }

    @Override
    public List<FeatureComponent> getComponents() {
        return List.of();
    }
}
