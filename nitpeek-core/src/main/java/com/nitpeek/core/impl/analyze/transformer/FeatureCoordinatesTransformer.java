package com.nitpeek.core.impl.analyze.transformer;

import com.nitpeek.core.api.common.TextCoordinate;
import com.nitpeek.core.api.common.TextSelection;
import com.nitpeek.core.api.util.FeatureTransformer;
import com.nitpeek.core.impl.common.SimpleFeatureComponent;

import java.util.function.UnaryOperator;

public final class FeatureCoordinatesTransformer extends ComponentTransformer implements FeatureTransformer {
    
    public static FeatureCoordinatesTransformer fromCoordinateTransform(UnaryOperator<TextCoordinate> coordinateTransform) {
        return new FeatureCoordinatesTransformer((TextSelection original) -> new TextSelection(coordinateTransform.apply(original.fromInclusive()), coordinateTransform.apply(original.toInclusive())));
    }

    public FeatureCoordinatesTransformer(UnaryOperator<TextSelection> selectionTransform) {
        super(original -> new SimpleFeatureComponent(original::getDescription,
                selectionTransform.apply(original.getCoordinates()),
                original.getRelevantTextPortion().orElse("")));
    }
}
