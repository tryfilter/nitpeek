package nitpeek.core.api.analyze;

import nitpeek.core.api.analyze.analyzer.Analyzer;
import nitpeek.core.api.analyze.analyzer.MissingPagesAnalyzer;
import org.junit.jupiter.api.BeforeEach;

final class MissingPagesAnalyzerPlainShould implements MissingPagesAnalyzerShould {


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
