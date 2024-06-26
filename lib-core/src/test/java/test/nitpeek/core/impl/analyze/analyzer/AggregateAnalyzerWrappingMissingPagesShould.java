package test.nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.impl.analyze.analyzer.AggregatingAnalyzer;
import nitpeek.core.impl.analyze.analyzer.MissingPages;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

final class AggregateAnalyzerWrappingMissingPagesShould implements MissingPagesShould {

    private Analyzer simpleAggregate;

    @BeforeEach
    void setup() {
        simpleAggregate = new AggregatingAnalyzer(List.of(new MissingPages()));
    }

    @Override
    public Analyzer getAnalyzer() {
        return simpleAggregate;
    }
}