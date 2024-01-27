package nitpeek.core.testutil;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.translate.DefaultFallbackEnglishTranslation;
import nitpeek.core.impl.translate.NoOpTranslation;
import org.junit.jupiter.api.Assertions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class FeatureAssert {

    private static Translation noop = new NoOpTranslation();
    private static Translation defaultEnglish = new DefaultFallbackEnglishTranslation();


    public static void assertFeaturesHaveCombinedExactlyCoordinates(Set<TextSelection> expectedCoordinates, List<Feature> features) {
        var actualCoordinates = new HashSet<TextSelection>();

        for (var feature : features) {
            actualCoordinates.addAll(feature.getComponents().stream().map(FeatureComponent::getCoordinates).toList());
        }

        Assertions.assertEquals(expectedCoordinates, actualCoordinates);
    }

    public static void assertEquivalentFeatures(List<Feature> expected, Analyzer replacer) {
        var actual = replacer.findFeatures();
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            var expectedFeature = expected.get(i);
            var actualFeature = actual.get(i);
            assertEquals(expectedFeature.getType(), actualFeature.getType());
            assertComponentsEquivalent(expectedFeature.getComponents(), actualFeature.getComponents());
        }
    }

    private static void assertComponentsEquivalent(List<FeatureComponent> expected, List<FeatureComponent> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertComponentsEquivalent(expected.get(i), actual.get(i));
        }
    }

    private static void assertComponentsEquivalent(FeatureComponent expected, FeatureComponent actual) {
        Assertions.assertAll(
                () -> assertEquals(expected.getCoordinates(), actual.getCoordinates()),
                () -> assertEquals(expected.getRelevantTextPortion(), actual.getRelevantTextPortion()),
                // we just want the expected value to be contained somehow, it doesn't have to be identical to the entire descriptionTranslationKey
                // using different translations here is a bit suspect
                () -> assertTrue(actual.getDescription(defaultEnglish).contains(expected.getDescription(noop)))
        );

    }
}
