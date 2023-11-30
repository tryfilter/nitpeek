package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.common.Feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class AggregatingAnalyzer implements Analyzer {

    private final Set<Analyzer> analyzers;

    public AggregatingAnalyzer(Set<Analyzer> analyzers) {
        this.analyzers = analyzers;
    }

    @Override
    public List<Feature> findFeatures() {
        List<Feature> features = new ArrayList<>();
        for (var analyzer : analyzers) {
            features.addAll(analyzer.findFeatures());
        }
        return features;
    }

    @Override
    public void processPage(TextPage page) {
        for (var analyzer : analyzers) {
            analyzer.processPage(page);
        }
    }
}
