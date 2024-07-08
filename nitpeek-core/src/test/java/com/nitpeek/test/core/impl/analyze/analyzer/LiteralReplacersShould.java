package com.nitpeek.test.core.impl.analyze.analyzer;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.FeatureComponent;
import com.nitpeek.core.api.common.TextCoordinate;
import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.impl.common.SimpleFeatureComponent;
import com.nitpeek.core.impl.common.StandardFeature;
import com.nitpeek.core.impl.process.ListPageConsumer;
import com.nitpeek.test.core.testutil.TestUtil;
import com.nitpeek.test.core.testutil.pagesource.HamletAct2ExcerptEdited;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.nitpeek.test.core.testutil.FeatureAssert;

import java.util.List;

interface LiteralReplacersShould {

    String NEW = "New Value";
    List<TextPage> pages = new ListPageConsumer(new HamletAct2ExcerptEdited()).getPages();

    Analyzer getCaseInsensitiveReplacer();

    Analyzer getCaseSensitiveReplacer();

    void setupReplacers(String oldValue, String newValue);

    @Test
    default void findNoFeaturesWhenLiteralNotPresentInText() {
        setupReplacement("ThisLiteralCannotBeFoundAnywhereInHamlet", NEW);

        assertBoth(List.of());
    }

    @Test
    default void findSingleFeatureWhenLiteralPresentOnceOnlyInsideLine() {
        var replace = "contracted";
        setupReplacement(replace, NEW);
        var textLocation = new TextCoordinate(0, 9, 6).extendToSelection(replace.length());
        assertBoth(List.of(replacerFeature(new SimpleFeatureComponent(NEW, textLocation, replace))));
    }

    @Test
    default void findFeaturesWhenLiteralPartOfWord() {
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
    default void findFeaturesAtEdgeOfLine() {
        var replace = "farewell";
        setupReplacement(replace, NEW);
        var textLocationDifferentCase = new TextCoordinate(1, 23, 0).extendToSelection(replace.length()); // "Farewell"
        var textLocationExactMatch = new TextCoordinate(2, 8, 34).extendToSelection(replace.length()); // "farewell"

        var featureDifferentCase = replacerFeature(new SimpleFeatureComponent(NEW, textLocationDifferentCase, "Farewell"));
        var featureExactMatch = replacerFeature(new SimpleFeatureComponent(NEW, textLocationExactMatch, replace));

        FeatureAssert.assertEquivalentFeatures(List.of(featureDifferentCase, featureExactMatch), getCaseInsensitiveReplacer());
        FeatureAssert.assertEquivalentFeatures(List.of(featureExactMatch), getCaseSensitiveReplacer());
    }

    @Test
    default void findEntireLine() {
        var replace = "    From whence though willingly I came to Denmark,";
        setupReplacement(replace, NEW);
        var textLocation = new TextCoordinate(3, 10, 0).extendToSelection(replace.length());

        assertBoth(List.of(replacerFeature(new SimpleFeatureComponent(NEW, textLocation, replace))));
    }

    @Test
    default void notSearchAcrossLines() {
        var replace = "natureThat";
        setupReplacement(replace, NEW);

        assertBoth(List.of());
    }

    private Feature replacerFeature(FeatureComponent... components) {
        return TestUtil.featureFromComponents(StandardFeature.REPLACE_LITERAL.getType(), components);
    }


    private void assertBoth(List<Feature> features) {
        Assertions.assertAll(
                () -> FeatureAssert.assertEquivalentFeatures(features, getCaseSensitiveReplacer()),
                () -> FeatureAssert.assertEquivalentFeatures(features, getCaseInsensitiveReplacer())
        );
    }


    private void setupReplacement(String oldValue, String newValue) {
        setupReplacers(oldValue, newValue);

        for (var page : pages) {
            getCaseInsensitiveReplacer().processPage(page);
            getCaseSensitiveReplacer().processPage(page);
        }
    }
}
