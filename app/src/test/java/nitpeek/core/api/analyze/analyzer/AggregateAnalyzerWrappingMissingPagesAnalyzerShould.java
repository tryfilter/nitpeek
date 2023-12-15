package nitpeek.core.api.analyze.analyzer;

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
