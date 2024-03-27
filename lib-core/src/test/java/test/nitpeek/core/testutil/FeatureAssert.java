package test.nitpeek.core.testutil;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.translate.DefaultFallbackEnglishTranslation;
import nitpeek.core.impl.translate.IdentityTranslation;
import org.junit.jupiter.api.Assertions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class FeatureAssert {

    private static final Translation noop = new IdentityTranslation();
    private static final Translation defaultEnglish = new DefaultFallbackEnglishTranslation();


    public static void assertFeaturesHaveCombinedExactlyCoordinates(Set<TextSelection> expectedCoordinates, List<Feature> features) {
        var actualCoordinates = new HashSet<TextSelection>();

        for (var feature : features) {
            actualCoordinates.addAll(feature.getComponents().stream().map(FeatureComponent::getCoordinates).toList());
        }

        Assertions.assertEquals(expectedCoordinates, actualCoordinates);
    }

    /**
     * Asserts for each feature component of each feature in {@code expected} that the corresponding feature component
     * produced by {@code replacer} has the same coordinates and relevant text portion and contains in its default English
     * translation the description of the component from {@code expected}.
     */
    public static void assertEquivalentFeatures(List<Feature> expected, Analyzer analyzer) {
        featureComparison(expected, analyzer.findFeatures(), FeatureAssert::assertComponentsEquivalent);
    }

    public static void assertEqualFeatures(List<Feature> expected, Analyzer analyzer, Translation i18n) {
        featureComparison(expected, analyzer.findFeatures(), (expect, actual) -> assertComponentsEqual(expect, actual, i18n));
    }

    private static void featureComparison(List<Feature> expected, List<Feature> actual, BiConsumer<FeatureComponent, FeatureComponent> equivalenceAssertion) {
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            var expectedFeature = expected.get(i);
            var actualFeature = actual.get(i);
            assertEquals(expectedFeature.getType(), actualFeature.getType());
            assertComponentsEquivalent(expectedFeature.getComponents(), actualFeature.getComponents(), equivalenceAssertion);
        }
    }

    private static void assertComponentsEquivalent(List<FeatureComponent> expected, List<FeatureComponent> actual, BiConsumer<FeatureComponent, FeatureComponent> equivalenceAssertion) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            equivalenceAssertion.accept(expected.get(i), actual.get(i));
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

    private static void assertComponentsEqual(FeatureComponent expected, FeatureComponent actual, Translation i18n) {
        Assertions.assertAll(
                () -> assertEquals(expected.getCoordinates(), actual.getCoordinates()),
                () -> assertEquals(expected.getRelevantTextPortion(), actual.getRelevantTextPortion()),
                () -> assertEquals(expected.getDescription(i18n), actual.getDescription(i18n))
        );
    }
}
