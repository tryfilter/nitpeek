package com.nitpeek.test.core.impl.analyze.analyzer;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.impl.analyze.analyzer.MissingPages;
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
