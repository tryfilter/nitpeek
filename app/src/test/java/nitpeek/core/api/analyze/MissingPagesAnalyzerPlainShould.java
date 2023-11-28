package nitpeek.core.api.analyze;

import org.junit.jupiter.api.BeforeEach;

final class MissingPagesAnalyzerPlainShould implements MissingPagesAnalyzerObservableShould {


    private Analyzer analyzer;

    @BeforeEach
    void setup() {
        analyzer = new MissingPagesAnalyzer();
    }


    @Override
    public Analyzer getAnalyzer() {
        return analyzer;
    }
}
