package nitpeek.core.api.analyze;

import nitpeek.core.api.analyze.analyzer.AggregatingAnalyzer;
import nitpeek.core.api.analyze.analyzer.Analyzer;
import nitpeek.core.api.analyze.analyzer.MissingPagesAnalyzer;
import org.junit.jupiter.api.BeforeEach;

import java.util.Set;

final class AggregateAnalyzerWrappingMissingPagesAnalyzerShould implements MissingPagesAnalyzerShould {

    private Analyzer simpleAggregate;

    @BeforeEach
    void setup() {
        simpleAggregate = new AggregatingAnalyzer(Set.of(new MissingPagesAnalyzer()));
    }

    @Override
    public Analyzer getAnalyzer() {
        return simpleAggregate;
    }
}
