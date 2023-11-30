package nitpeek.core.api.analyze;

import nitpeek.core.api.analyze.analyzer.AggregatingAnalyzer;
import nitpeek.core.api.analyze.analyzer.MissingPagesAnalyzer;
import nitpeek.core.api.analyze.analyzer.PageCounterAnalyzer;
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
        var pageCounter = new PageCounterAnalyzer();
        var missingPages = new MissingPagesAnalyzer();

        var aggregate = new AggregatingAnalyzer(Set.of(new PageCounterAnalyzer(), new MissingPagesAnalyzer()));

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
