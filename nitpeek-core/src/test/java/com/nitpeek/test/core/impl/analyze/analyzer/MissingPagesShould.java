package com.nitpeek.test.core.impl.analyze.analyzer;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.api.common.*;
import com.nitpeek.core.api.util.PageRange;
import com.nitpeek.core.impl.common.StandardFeature;
import com.nitpeek.core.internal.Confidence;
import org.junit.jupiter.api.Test;
import com.nitpeek.test.core.testutil.TestUtil;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

interface MissingPagesShould {

    double COMPARE_DOUBLE_DELTA = 0.0000001;

    Analyzer getAnalyzer();

    @Test
    default void reportThatNoPagesWereAnalyzedHighConfidence() {
        // no pages processed
        assertSingleFeature(Confidence.HIGH);
    }

    @Test
    default void reportNothingIfFirstPageWasAnalyzed() {
        var analyzer = getAnalyzer();
        TextPage firstPage = TestUtil.emptyPage(0);
        processPages(firstPage);
        assertEquals(emptyList(), analyzer.findFeatures());
    }

    @Test
    default void reportMissingPagesIfFirstPageWasNotAnalyzedMediumConfidence() {
        TextPage secondPage = TestUtil.emptyPage(1);
        processPages(secondPage);
        assertSingleFeature(Confidence.MEDIUM);
    }

    @Test
    default void reportMissingPagesIfSeveralContiguousFirstPagesWereNotAnalyzedMediumConfidence() {
        TextPage page10 = TestUtil.emptyPage(10);
        TextPage page11 = TestUtil.emptyPage(11);
        TextPage page12 = TestUtil.emptyPage(12);
        processPages(page10, page11, page12);
        assertSingleFeature(Confidence.MEDIUM);
    }

    @Test
    default void reportMissingPageIfMiddlePageWasNotAnalyzedHighConfidence() {
        TextPage firstPage = TestUtil.emptyPage(0);
        TextPage thirdPage = TestUtil.emptyPage(2);
        processPages(firstPage, thirdPage);
        assertSingleFeature(Confidence.HIGH);
    }

    @Test
    default void reportEachPortionOfMissingPagesIfMiddlePagesWereNotAnalyzedHighConfidence() {
        TextPage firstPage = TestUtil.emptyPage(0);
        TextPage thirdPage = TestUtil.emptyPage(2);
        TextPage fourthPage = TestUtil.emptyPage(3);
        TextPage sixthPage = TestUtil.emptyPage(5);
        processPages(firstPage, thirdPage, fourthPage, sixthPage);

        assertFeatures(Confidence.HIGH, 2);
    }

    @Test
    default void reportEachPortionOfContiguousMissingPagesIfMiddlePagesWereNotAnalyzedHighConfidence() {
        TextPage page0 = TestUtil.emptyPage(0);
        TextPage page4 = TestUtil.emptyPage(4);
        TextPage page5 = TestUtil.emptyPage(5);
        TextPage page10 = TestUtil.emptyPage(10);
        processPages(page0, page4, page5, page10);

        assertFeatures(Confidence.HIGH, 2);
    }

    @Test
    default void reportMissingFirstPageDetailsInFeatureComponents() {
        var analyzer = getAnalyzer();
        TextPage secondPage = TestUtil.emptyPage(1);

        processPages(secondPage);

        Feature onlyFeature = analyzer.findFeatures().getFirst();
        assertMissingPages(onlyFeature, pages(0, 0));

    }

    @Test
    default void reportMissingPagesDetailsInFeatureComponents() {
        var analyzer = getAnalyzer();
        TextPage page0 = TestUtil.emptyPage(0);
        TextPage page4 = TestUtil.emptyPage(4);
        TextPage page5 = TestUtil.emptyPage(5);
        TextPage page12 = TestUtil.emptyPage(12);
        TextPage page14 = TestUtil.emptyPage(14);
        processPages(page0, page4, page5, page12, page14);

        List<Feature> features = analyzer.findFeatures();
        Feature missingFrom1To3 = features.get(0); // missing pages between page 0 and page 4
        Feature missingFrom6To11 = features.get(1); // missing pages between page 5 and page 12
        Feature missing13 = features.get(2); // missing page between page 12 and page 14

        assertMissingPages(missingFrom1To3, pages(1, 3));
        assertMissingPages(missingFrom6To11, pages(6, 11));
        assertMissingPages(missing13, pages(13, 13));
    }

    private void processPages(TextPage... pages) {
        var analyzer = getAnalyzer();
        for (var page : pages) analyzer.processPage(page);
    }

    private void assertSingleFeature(Confidence confidence) {
        assertFeatures(confidence, 1);
    }

    private void assertFeatures(Confidence confidence, int featureCount) {
        var analyzer = getAnalyzer();
        List<Feature> features = analyzer.findFeatures();
        assertEquals(featureCount, features.size());

        for (Feature feature : features) {
            assertEquals(StandardFeature.MISSING_PAGES.getType(), feature.getType());
            assertEquals(confidence.value(), feature.getConfidence(), COMPARE_DOUBLE_DELTA);
        }
    }

    private static TextSelection pages(int firstPage, int lastPage) {

        if (firstPage > lastPage)
            throw new IllegalArgumentException("firstPage must be <= than lastPage, but were " + firstPage + ", respectively " + lastPage);
        return new TextSelection(new TextCoordinate(firstPage, 0, 0), new TextCoordinate(lastPage, 0, 0));
    }

    private static void assertMissingPages(Feature feature, TextSelection... coordinates) {

        List<PageRange> expectedCoordinates = Arrays.stream(coordinates).map(PageRange::pageRange).toList();
        List<PageRange> actualCoordinates = feature.getComponents().stream().map(FeatureComponent::getCoordinates).map(PageRange::pageRange).toList();

        assertEquals(expectedCoordinates, actualCoordinates);
    }
}
