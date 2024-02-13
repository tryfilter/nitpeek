package test.nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.FeatureNotifier;
import nitpeek.core.api.analyze.NotifyingAnalyzer;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.impl.analyze.analyzer.ObservableAnalyzer;

import java.util.List;

import static test.nitpeek.core.testutil.TestUtil.emptyPage;

interface ObservableAnalyzerMissingPagesShould extends NotifyingAnalyzersShould, MissingPagesShould {

    ObservableAnalyzer getObservableAnalyzer();

    @Override
    default FeatureNotifier getNotifier() {
        return getObservableAnalyzer();
    }

    @Override
    default NotifyingAnalyzer getAnalyzer() {
        return getObservableAnalyzer();
    }

    @Override
    default List<TextPage> getPages() {
        // produce MissingPages features for [0-2], [5-5], [8-9], [11-11]
        return List.of(
                emptyPage(3),
                emptyPage(4),
                emptyPage(6),
                emptyPage(7),
                emptyPage(10),
                emptyPage(12),
                emptyPage(13),
                emptyPage(14)
        );
    }
}
