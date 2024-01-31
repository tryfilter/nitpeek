package nitpeek.core.impl.analyze.transformer;

import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.util.FeatureTransformer;
import nitpeek.core.impl.common.SimpleFeatureComponent;

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
