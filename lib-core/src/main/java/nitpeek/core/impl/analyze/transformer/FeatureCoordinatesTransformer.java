package nitpeek.core.impl.analyze.transformer;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.util.FeatureTransformer;
import nitpeek.core.impl.common.SimpleFeature;
import nitpeek.core.impl.common.SimpleFeatureComponent;

import java.util.function.UnaryOperator;

public final class FeatureCoordinatesTransformer implements FeatureTransformer {

    private final UnaryOperator<TextSelection> selectionTransform;

    public static FeatureCoordinatesTransformer fromCoordinateTransform(UnaryOperator<TextCoordinate> coordinateTransform) {
        return new FeatureCoordinatesTransformer((TextSelection original) -> new TextSelection(coordinateTransform.apply(original.fromInclusive()), coordinateTransform.apply(original.toInclusive())));
    }
    public FeatureCoordinatesTransformer(UnaryOperator<TextSelection> selectionTransform) {
        this.selectionTransform = selectionTransform;
    }

    @Override
    public Feature transform(Feature original) {
        return new SimpleFeature(original.getType(), original.getComponents().stream().map(this::transformComponent).toList(), original.getConfidence());
    }

    private FeatureComponent transformComponent(FeatureComponent component) {
        return new SimpleFeatureComponent(component::getDescription, selectionTransform.apply(component.getCoordinates()), component.getRelevantTextPortion().orElse(""));
    }
}
