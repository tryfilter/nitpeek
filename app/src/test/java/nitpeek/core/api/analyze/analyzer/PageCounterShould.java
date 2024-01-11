package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.common.util.PageRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nitpeek.core.testutil.TestUtil.emptyPage;

final class PageCounterShould {

    private PageCounter analyzer;

    @BeforeEach
    void setup() {
        analyzer = new PageCounter();
    }

    @Test
    void produceSingleComponentWhenNoPagesAreProcessed() {
        List<Feature> features = analyzer.findFeatures();
        Assertions.assertEquals(1, features.size());

        List<FeatureComponent> components = features.getFirst().getComponents();
        Assertions.assertEquals(1, components.size());

        TextSelection textRange = components.getFirst().getCoordinates();
        assertSinglePage(0, textRange);
    }

    @Test
    void produceTwoComponentsWhenNoPagesAreMissing() {

        analyzer.processPage(emptyPage(0));
        analyzer.processPage(emptyPage(1));
        analyzer.processPage(emptyPage(2));

        List<Feature> features = analyzer.findFeatures();
        Assertions.assertEquals(1, features.size());

        List<FeatureComponent> components = features.getFirst().getComponents();
        Assertions.assertEquals(2, components.size());

        // first component is summary of results
        TextSelection textRangeSummary = components.getFirst().getCoordinates();
        assertPageRange(0, 2, textRangeSummary);

        // each subsequent component describes a contiguous range of pages in detail
        TextSelection textRangeDetails = components.getLast().getCoordinates();
        assertPageRange(0, 2, textRangeDetails);
    }

    @Test
    void produceTwoComponentsWhenSinglePageIsProcessed() {


        analyzer.processPage(emptyPage(42));

        List<Feature> features = analyzer.findFeatures();
        Assertions.assertEquals(1, features.size());

        List<FeatureComponent> components = features.getFirst().getComponents();
        Assertions.assertEquals(2, components.size());

        TextSelection textRangeSummary = components.getFirst().getCoordinates();
        assertSinglePage(42, textRangeSummary);

        TextSelection textRangeDetails = components.getLast().getCoordinates();
        assertSinglePage(42, textRangeDetails);
    }

    @Test
    void produceOneComponentForEachIsolatedPage() {

        analyzer.processPage(emptyPage(0));
        analyzer.processPage(emptyPage(2));
        analyzer.processPage(emptyPage(4));

        List<Feature> features = analyzer.findFeatures();
        Assertions.assertEquals(1, features.size());

        List<FeatureComponent> components = features.getFirst().getComponents();
        Assertions.assertEquals(4, components.size());

        TextSelection textRangeSummary = components.getFirst().getCoordinates();
        assertPageRange(0, 4, textRangeSummary);

        TextSelection page0 = components.get(1).getCoordinates();
        assertSinglePage(0, page0);

        TextSelection page2 = components.get(2).getCoordinates();
        assertSinglePage(2, page2);

        TextSelection page4 = components.get(3).getCoordinates();
        assertSinglePage(4, page4);
    }

    @Test
    void produceOneComponentForEachPageRange() {

        analyzer.processPage(emptyPage(0));
        analyzer.processPage(emptyPage(1));
        analyzer.processPage(emptyPage(4));
        analyzer.processPage(emptyPage(5));
        analyzer.processPage(emptyPage(8));
        analyzer.processPage(emptyPage(9));
        analyzer.processPage(emptyPage(10));
        analyzer.processPage(emptyPage(11));
        analyzer.processPage(emptyPage(12));
        analyzer.processPage(emptyPage(13));

        List<Feature> features = analyzer.findFeatures();
        Assertions.assertEquals(1, features.size());

        List<FeatureComponent> components = features.getFirst().getComponents();
        Assertions.assertEquals(4, components.size());

        TextSelection textRangeSummary = components.getFirst().getCoordinates();
        assertPageRange(0, 13, textRangeSummary);

        TextSelection pages0and1 = components.get(1).getCoordinates();
        assertPageRange(0, 1, pages0and1);

        TextSelection pages4and5 = components.get(2).getCoordinates();
        assertPageRange(4, 5, pages4and5);

        TextSelection pages8thru13 = components.get(3).getCoordinates();
        assertPageRange(8, 13, pages8thru13);
    }

    @Test
    void produceOneComponentForEachSinglePageAndEachRange() {

        analyzer.processPage(emptyPage(3));
        analyzer.processPage(emptyPage(4));
        analyzer.processPage(emptyPage(6));
        analyzer.processPage(emptyPage(8));
        analyzer.processPage(emptyPage(9));
        analyzer.processPage(emptyPage(11));
        analyzer.processPage(emptyPage(13));
        analyzer.processPage(emptyPage(15));

        List<Feature> features = analyzer.findFeatures();
        Assertions.assertEquals(1, features.size());

        List<FeatureComponent> components = features.getFirst().getComponents();
        Assertions.assertEquals(7, components.size());

        TextSelection textRangeSummary = components.getFirst().getCoordinates();
        assertPageRange(3, 15, textRangeSummary);

        TextSelection pages3and4 = components.get(1).getCoordinates();
        assertPageRange(3, 4, pages3and4);

        TextSelection page6 = components.get(2).getCoordinates();
        assertSinglePage(6, page6);

        TextSelection pages8and9 = components.get(3).getCoordinates();
        assertPageRange(8, 9, pages8and9);

        TextSelection page11 = components.get(4).getCoordinates();
        assertSinglePage(11, page11);

        TextSelection page13 = components.get(5).getCoordinates();
        assertSinglePage(13, page13);

        TextSelection page15 = components.get(6).getCoordinates();
        assertSinglePage(15, page15);
    }

    private void assertSinglePage(int expectedPage, TextSelection textSelection) {
        assertPageRange(expectedPage, expectedPage, textSelection);
    }

    private void assertPageRange(int expectedFirstPage, int expectedLastPage, TextSelection textSelection) {
        Assertions.assertEquals(new PageRange(expectedFirstPage, expectedLastPage), PageRange.pageRange(textSelection));
    }
}
