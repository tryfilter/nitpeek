package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.analyze.transformer.Transformer;
import nitpeek.core.api.common.Feature;

import java.util.List;

public final class TransformingAnalyzer implements Analyzer {

    private final Analyzer analyzer;
    private final Transformer transformer;

    public TransformingAnalyzer(Analyzer analyzer, Transformer transformer) {
        this.analyzer = analyzer;
        this.transformer = transformer;
    }

    @Override
    public List<Feature> findFeatures() {
        return analyzer.findFeatures().stream().map(transformer::transformFeature).toList();
    }

    @Override
    public void processPage(TextPage page) {
        analyzer.processPage(transformer.transformPage(page));
    }
}
