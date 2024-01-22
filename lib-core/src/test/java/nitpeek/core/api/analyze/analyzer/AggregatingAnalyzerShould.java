package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.impl.analyze.analyzer.AggregatingAnalyzer;
import nitpeek.core.impl.analyze.analyzer.MissingPages;
import nitpeek.core.impl.analyze.analyzer.PageCounter;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.common.Feature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static nitpeek.core.testutil.TestUtil.emptyPage;

final class AggregatingAnalyzerShould {

    private static final List<TextPage> pages = List.of(
            emptyPage(3),
            emptyPage(4),
            emptyPage(6),
            emptyPage(9)
    );

    @Test
    void aggregateResults() {
        var pageCounter = new PageCounter();
        var missingPages = new MissingPages();

        var aggregate = new AggregatingAnalyzer(Set.of(new PageCounter(), new MissingPages()));

        for (var page : pages) {
            pageCounter.processPage(page);
            missingPages.processPage(page);
            aggregate.processPage(page);
        }

        Set<Feature> expected = new HashSet<>();
        expected.addAll(pageCounter.findFeatures());
        expected.addAll(missingPages.findFeatures());

        Set<Feature> actual = new HashSet<>(aggregate.findFeatures());

        Assertions.assertEquals(expected, actual);
    }
}
