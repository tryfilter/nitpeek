package test.nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.impl.analyze.analyzer.MissingPages;
import nitpeek.core.impl.analyze.analyzer.ObservableAnalyzer;
import nitpeek.core.impl.analyze.notify.ThreadsafeFeatureNotifier;
import org.junit.jupiter.api.BeforeEach;

final class ObservableAnalyzerWrappingMissingPagesWithCustomNotifierShould implements ObservableAnalyzerMissingPagesShould {
    private ObservableAnalyzer analyzer;

    @BeforeEach
    void setup() {
        analyzer = new ObservableAnalyzer(new MissingPages(), new ThreadsafeFeatureNotifier());
    }


    @Override
    public ObservableAnalyzer getObservableAnalyzer() {
        return analyzer;
    }
}
