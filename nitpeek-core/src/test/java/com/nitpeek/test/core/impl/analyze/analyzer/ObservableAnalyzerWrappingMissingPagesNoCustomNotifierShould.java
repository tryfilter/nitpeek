package com.nitpeek.test.core.impl.analyze.analyzer;

import com.nitpeek.core.impl.analyze.analyzer.MissingPages;
import com.nitpeek.core.impl.analyze.analyzer.ObservableAnalyzer;
import org.junit.jupiter.api.BeforeEach;

final class ObservableAnalyzerWrappingMissingPagesNoCustomNotifierShould implements ObservableAnalyzerMissingPagesShould {
    private ObservableAnalyzer analyzer;

    @BeforeEach
    void setup() {
        analyzer = new ObservableAnalyzer(new MissingPages());
    }


    @Override
    public ObservableAnalyzer getObservableAnalyzer() {
        return analyzer;
    }
}
