package com.nitpeek.core.impl.analyze.analyzer;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.TextPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregates features from one or more wrapped analyzers.<br>
 * <br>
 * This Analyzer is NOT thread safe.<br>
 * Order dependence and repeat processing tolerance of this analyzer depend on the characteristics of the wrapped analyzers.<br>
 * <br>
 * This is a decorator.
 */
public final class AggregatingAnalyzer implements Analyzer {

    private final List<? extends Analyzer> analyzers;

    public AggregatingAnalyzer(List<? extends Analyzer> analyzers) {
        this.analyzers = analyzers;
    }

    @Override
    public List<Feature> findFeatures() {
        List<Feature> features = new ArrayList<>();
        for (var analyzer : analyzers) {
            features.addAll(analyzer.findFeatures());
        }
        return Collections.unmodifiableList(features);
    }

    @Override
    public void processPage(TextPage page) {
        for (var analyzer : analyzers) {
            analyzer.processPage(page);
        }
    }
}