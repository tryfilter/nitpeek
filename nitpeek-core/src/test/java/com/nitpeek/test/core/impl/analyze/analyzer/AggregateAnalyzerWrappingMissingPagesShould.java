package com.nitpeek.test.core.impl.analyze.analyzer;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.impl.analyze.analyzer.AggregatingAnalyzer;
import com.nitpeek.core.impl.analyze.analyzer.MissingPages;
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