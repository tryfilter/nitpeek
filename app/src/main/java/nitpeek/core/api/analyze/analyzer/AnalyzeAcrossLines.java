package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.analyze.transformer.LineJoiner;
import nitpeek.core.api.common.Feature;

import java.util.List;


public final class AnalyzeAcrossLines implements Analyzer {

    private final Analyzer crossLineAnalyzer;

    /**
     * @param analyzer the analyzer to wrap: the wrapped analyzer will receive all pages passed to the wrapping analyzer
     *                 as single-line pages; this way, any analysis it performs disregards line separation, without the
     *                 being aware of this
     */
    public AnalyzeAcrossLines(Analyzer analyzer) {
        this.crossLineAnalyzer = new TransformingAnalyzer(analyzer, new LineJoiner());
    }

    /**
     * @return a list of features produced by the wrapped analyzer, transformed to correctly relate to the pages
     * originally passed to the wrapping analyzer
     */
    @Override
    public List<Feature> findFeatures() {
        return crossLineAnalyzer.findFeatures();
    }

    @Override
    public void processPage(TextPage page) {
        crossLineAnalyzer.processPage(page);
    }
}
