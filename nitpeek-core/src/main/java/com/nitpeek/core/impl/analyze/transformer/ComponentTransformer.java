package com.nitpeek.core.impl.analyze.transformer;

import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.FeatureComponent;
import com.nitpeek.core.api.util.FeatureTransformer;
import com.nitpeek.core.impl.common.SimpleFeature;

import java.util.function.UnaryOperator;

public sealed class ComponentTransformer implements FeatureTransformer permits FeatureCoordinatesTransformer {

    private final UnaryOperator<FeatureComponent> transformComponent;

    public ComponentTransformer(UnaryOperator<FeatureComponent> componentTransformer) {
        this.transformComponent = componentTransformer;
    }

    @Override
    public final Feature transform(Feature original) {
        return new SimpleFeature(original.getType(),
                original.getComponents().stream().map(transformComponent).toList(),
                original.getConfidence()
        );
    }
}
