package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.notify.ThreadsafeFeatureNotifier;
import org.junit.jupiter.api.BeforeEach;

final class ObservableAnalyzerWrappingMissingPagesWithCustomNotifierShould implements ObservableAnalyzerMissingPagesShould {
    private ObservableAnalyzer analyzer;

    @BeforeEach
    void setup() {
        analyzer = new ObservableAnalyzer(new MissingPagesAnalyzer(), new ThreadsafeFeatureNotifier());
    }


    @Override
    public ObservableAnalyzer getObservableAnalyzer() {
        return analyzer;
    }
}
