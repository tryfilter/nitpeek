package nitpeek.core.api.analyze;

import org.junit.jupiter.api.BeforeEach;

final class ObservableAnalyzerMissingPagesNoCustomNotifierShould implements ObservableAnalyzerMissingPagesShould {
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
