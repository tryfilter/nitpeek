package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.impl.analyze.analyzer.MissingPages;
import nitpeek.core.impl.analyze.analyzer.ObservableAnalyzer;
import org.junit.jupiter.api.BeforeEach;

final class ObservableAnalyzerWrappingMissingPagesNoCustomNotifierShould implements ObservableAnalyzerMissingPagesShould {
    private ObservableAnalyzer analyzer;

    @BeforeEach
    void setup() {
        analyzer = new ObservableAnalyzer(new MissingPages());
    }


    @Override
    public ObservableAnalyzer getObservableAnalyzer() {
        return analyzer;
    }
}