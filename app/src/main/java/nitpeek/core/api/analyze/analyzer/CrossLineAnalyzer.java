package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.analyze.transformer.LineJoiner;
import nitpeek.core.api.common.Feature;

import java.util.List;

/**
 * Transparently allows a wrapped analyzer that normally only works inside single lines to cross line boundaries.<br>
 * <br>
 * This Analyzer is NOT thread safe.<br>
 * Processing-order dependence and repeat processing tolerance of this analyzer depend on the characteristics of the
 * wrapped analyzer.<br>
 * This is a decorator.
 */
public final class CrossLineAnalyzer implements Analyzer {

    private final Analyzer analyzeAcrossLines;

    /**
     * @param analyzer the analyzer to wrap: the wrapped analyzer will receive all pages passed to the wrapping analyzer
     *                 as single-line pages; this way, any analysis it performs disregards line separation, without it
     *                 being aware of this
     */
    public CrossLineAnalyzer(Analyzer analyzer) {
        this.analyzeAcrossLines = new TransformingAnalyzer(analyzer, new LineJoiner());
    }

    /**
     * @return a list of features produced by the wrapped analyzer, transformed to correctly relate to the pages
     * originally passed to the wrapping analyzer
     */
    @Override
    public List<Feature> findFeatures() {
        return analyzeAcrossLines.findFeatures();
    }

    @Override
    public void processPage(TextPage page) {
        analyzeAcrossLines.processPage(page);
    }
}
