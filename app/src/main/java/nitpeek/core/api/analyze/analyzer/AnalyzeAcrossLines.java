package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.analyze.transformer.LineJoiner;
import nitpeek.core.api.analyze.transformer.Transformer;
import nitpeek.core.api.common.Feature;

import java.util.List;

public class AnalyzeAcrossLines implements Analyzer {

    private final Analyzer analyzer;
    private final Transformer transformer = new LineJoiner();

    public AnalyzeAcrossLines(Analyzer analyzer) {
        this.analyzer = analyzer;
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
