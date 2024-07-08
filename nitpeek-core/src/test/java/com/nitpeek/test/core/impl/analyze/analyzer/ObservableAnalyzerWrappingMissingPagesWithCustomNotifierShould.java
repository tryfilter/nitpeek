package com.nitpeek.test.core.impl.analyze.analyzer;

import com.nitpeek.core.impl.analyze.analyzer.MissingPages;
import com.nitpeek.core.impl.analyze.analyzer.ObservableAnalyzer;
import com.nitpeek.core.impl.analyze.notify.ThreadsafeFeatureNotifier;
import org.junit.jupiter.api.BeforeEach;

final class ObservableAnalyzerWrappingMissingPagesWithCustomNotifierShould implements ObservableAnalyzerMissingPagesShould {
    private ObservableAnalyzer analyzer;

    @BeforeEach
    void setup() {
        analyzer = new ObservableAnalyzer(new MissingPages(), new ThreadsafeFeatureNotifier());
    }


    @Override
    public ObservableAnalyzer getObservableAnalyzer() {
        return analyzer;
    }
}
