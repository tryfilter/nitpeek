package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.impl.analyze.analyzer.MissingPages;
import nitpeek.core.api.analyze.Analyzer;
import org.junit.jupiter.api.BeforeEach;

final class MissingPagesPlainShould implements MissingPagesShould {


    private Analyzer analyzer;

    @BeforeEach
    void setup() {
        analyzer = new MissingPages();
    }


    @Override
    public Analyzer getAnalyzer() {
        return analyzer;
    }
}
