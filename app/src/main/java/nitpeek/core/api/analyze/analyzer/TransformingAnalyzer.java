package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.analyze.transformer.Transformer;
import nitpeek.core.api.common.Feature;

import java.util.List;

/**
 * Alters the behavior of an existing analyzer by transforming pages before passing them on to the wrapped analyzer and
 * transforming features produced by the wrapped analyzer before returning them. Uses the passed transformer to
 * perform both these transformations.<br>
 * <br>
 * Thread safety and processing-order dependence are determined by the characteristics of the wrapped analyzer and the
 * employed transformer.<br>
 * This is a decorator.
 */
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
