package nitpeek.core.api.analyze;

import nitpeek.core.api.analyze.analyzer.ObservableAnalyzer;
import nitpeek.core.api.analyze.notify.FeatureNotifier;
import nitpeek.core.api.analyze.notify.NotifyingAnalyzer;

import java.util.List;

import static nitpeek.core.testutil.TestUtil.emptyPage;

interface ObservableAnalyzerMissingPagesShould extends NotifyingAnalyzersShould, MissingPagesAnalyzerShould {

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
