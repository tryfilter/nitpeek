package nitpeek.core.testutil;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.TextSelection;
import org.junit.jupiter.api.Assertions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FeatureAssert {


    public static void assertFeaturesHaveCombinedExactlyCoordinates(Set<TextSelection> expectedCoordinates, List<Feature> features) {
        var actualCoordinates = new HashSet<TextSelection>();

        for (var feature : features) {
            actualCoordinates.addAll(feature.getComponents().stream().map(FeatureComponent::getCoordinates).toList());
        }

        Assertions.assertEquals(expectedCoordinates, actualCoordinates);
    }
}
