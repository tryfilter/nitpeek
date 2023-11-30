package nitpeek.core.api.analyze;

import nitpeek.core.api.analyze.analyzer.MissingPagesAnalyzer;
import nitpeek.core.api.analyze.analyzer.ObservableAnalyzer;
import org.junit.jupiter.api.BeforeEach;

final class ObservableAnalyzerWrappingMissingPagesNoCustomNotifierShould implements ObservableAnalyzerMissingPagesShould {
    private ObservableAnalyzer analyzer;

    @BeforeEach
    void setup() {
        analyzer = new ObservableAnalyzer(new MissingPagesAnalyzer());
    }


    @Override
    public ObservableAnalyzer getObservableAnalyzer() {
        return analyzer;
    }
}
