package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.analyze.analyzer.Analyzer;
import nitpeek.core.api.analyze.analyzer.LiteralReplacer;
import nitpeek.core.api.common.*;
import nitpeek.core.api.process.ListPageConsumer;
import nitpeek.core.testutil.pagesource.HamletAct2ExcerptEdited;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class LiteralReplacerShould {

    private final List<TextPage> pages = new ListPageConsumer(new HamletAct2ExcerptEdited()).getPages();

    private Analyzer replacerCaseSensitive;
    private Analyzer replacerIgnoreCase;

    private static final String NEW = "New Value";


    @BeforeEach
    void setup() {

    }

    @Test
    void findNoFeaturesWhenLiteralNotPresentInText() {
        setupReplacement("ThisLiteralCannotBeFoundAnywhereInHamlet", NEW);

        assertBoth(List.of());
    }

    @Test
    void findSingleFeatureWhenLiteralPresentOnceOnlyInsideLine() {
        var replace = "contracted";
        setupReplacement(replace, NEW);
        var textLocation = new TextCoordinate(0, 9, 6).extendToSelection(replace.length());
        assertBoth(List.of(replacerFeature(new SimpleFeatureComponent(NEW, textLocation, replace))));
    }

    @Test
    void findFeaturesWhenLiteralPartOfWord() {
        var replace = "think";
        setupReplacement(replace, NEW);
        var textLocation1 = new TextCoordinate(0, 11, 27).extendToSelection(replace.length()); // "think"
        var textLocation2 = new TextCoordinate(1, 3, 3).extendToSelection(replace.length()); // "thinking"
        assertBoth(List.of(
                        replacerFeature(new SimpleFeatureComponent(NEW, textLocation1, replace)),
                        replacerFeature(new SimpleFeatureComponent(NEW, textLocation2, replace))
                )
        );
    }

    @Test
    void findFeaturesAtEdgeOfLine() {
        var replace = "farewell";
        setupReplacement(replace, NEW);
        var textLocationDifferentCase = new TextCoordinate(1, 23, 0).extendToSelection(replace.length()); // "Farewell"
        var textLocationExactMatch = new TextCoordinate(2, 8, 34).extendToSelection(replace.length()); // "farewell"

        var featureDifferentCase = replacerFeature(new SimpleFeatureComponent(NEW, textLocationDifferentCase, replace));
        var featureExactMatch = replacerFeature(new SimpleFeatureComponent(NEW, textLocationExactMatch, replace));

        assertEquivalentFeatures(List.of(featureDifferentCase, featureExactMatch), replacerIgnoreCase);
        assertEquivalentFeatures(List.of(featureExactMatch), replacerCaseSensitive);
    }

    @Test
    void findEntireLine() {
        var replace = "    From whence though willingly I came to Denmark,";
        setupReplacement(replace, NEW);
        var textLocation = new TextCoordinate(3, 10, 0).extendToSelection(replace.length());

        assertBoth(List.of(replacerFeature(new SimpleFeatureComponent(NEW, textLocation, replace))));
    }

    @Test
    void notSearchAcrossLines() {
        var replace = "natureThat";
        setupReplacement(replace, NEW);

        assertBoth(List.of());
    }

    private Feature replacerFeature(FeatureComponent... components) {
        return new SimpleFeature(StandardFeature.REPLACE_LITERAL.getType(), Arrays.asList(components), 0);
    }


    private void assertBoth(List<Feature> features) {
        Assertions.assertAll(
                () -> assertEquivalentFeatures(features, replacerCaseSensitive),
                () -> assertEquivalentFeatures(features, replacerIgnoreCase)
        );
    }

    private void assertEquivalentFeatures(List<Feature> expected, Analyzer replacer) {
        var actual = replacer.findFeatures();
        assertEquals(expected.size(), actual.size());

        for (int i = 0; i < expected.size(); i++) {
            var expectedFeature = expected.get(i);
            var actualFeature = actual.get(i);
            assertEquals(expectedFeature.getType(), actualFeature.getType());
            assertComponentsEquivalent(expectedFeature.getComponents(), actualFeature.getComponents());
        }
    }

    private void assertComponentsEquivalent(List<FeatureComponent> expected, List<FeatureComponent> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertComponentsEquivalent(expected.get(i), actual.get(i));
        }
    }

    private void assertComponentsEquivalent(FeatureComponent expected, FeatureComponent actual) {
        Assertions.assertAll(
                () -> assertEquals(expected.getCoordinates(), actual.getCoordinates()),
                () -> assertEquals(expected.getRelevantTextPortion(), actual.getRelevantTextPortion()),
                // we just want the newValue to be mentioned somehow, we don't care exactly in what manner
                () -> assertTrue(actual.getDescription().contains(expected.getDescription()))
        );

    }

    private void setupReplacement(String oldValue, String newValue) {
        replacerCaseSensitive = new LiteralReplacer(oldValue, newValue, false);
        replacerIgnoreCase = new LiteralReplacer(oldValue, newValue, true);

        for (var page : pages) {
            replacerCaseSensitive.processPage(page);
            replacerIgnoreCase.processPage(page);
        }
    }
}