package test.nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.impl.analyze.analyzer.AggregatingAnalyzer;
import nitpeek.core.impl.analyze.analyzer.MissingPages;
import nitpeek.core.impl.analyze.analyzer.PageCounter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import test.nitpeek.core.testutil.TestUtil;

import java.util.ArrayList;
import java.util.List;

final class AggregatingAnalyzerShould {

    private static final List<TextPage> pages = List.of(
            TestUtil.emptyPage(3),
            TestUtil.emptyPage(4),
            TestUtil.emptyPage(6),
            TestUtil.emptyPage(9)
    );

    @Test
    void aggregateResultsInOrder() {
        var pageCounter = new PageCounter();
        var missingPages = new MissingPages();

        var aggregate = new AggregatingAnalyzer(List.of(new PageCounter(), new MissingPages()));

        for (var page : pages) {
            pageCounter.processPage(page);
            missingPages.processPage(page);
            aggregate.processPage(page);
        }

        List<Feature> expected = new ArrayList<>();
        expected.addAll(pageCounter.findFeatures());
        expected.addAll(missingPages.findFeatures());

        List<Feature> actual = new ArrayList<>(aggregate.findFeatures());

        Assertions.assertEquals(expected, actual);
    }
}