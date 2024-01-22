package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.impl.analyze.analyzer.AggregatingAnalyzer;
import nitpeek.core.impl.analyze.analyzer.MissingPages;
import nitpeek.core.api.analyze.Analyzer;
import org.junit.jupiter.api.BeforeEach;

import java.util.Set;

final class AggregateAnalyzerWrappingMissingPagesShould implements MissingPagesShould {

    private Analyzer simpleAggregate;

    @BeforeEach
    void setup() {
        simpleAggregate = new AggregatingAnalyzer(Set.of(new MissingPages()));
    }

    @Override
    public Analyzer getAnalyzer() {
        return simpleAggregate;
    }
}
