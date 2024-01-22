package nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.common.Feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Aggregates features from one or more wrapped analyzers.<br>
 * <br>
 * This Analyzer is NOT thread safe.<br>
 * Order dependence and repeat processing tolerance of this analyzer depend on the characteristics of the wrapped analyzers.<br>
 * <br>
 * This is a decorator.
 */
public final class AggregatingAnalyzer implements Analyzer {

    private final Set<Analyzer> analyzers;

    public AggregatingAnalyzer(Set<Analyzer> analyzers) {
        this.analyzers = analyzers;
    }

    /**
     * @return an unmodifiable snapshot
     */
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
