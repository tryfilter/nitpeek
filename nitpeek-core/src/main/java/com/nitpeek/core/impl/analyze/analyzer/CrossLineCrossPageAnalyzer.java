package com.nitpeek.core.impl.analyze.analyzer;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.api.analyze.AnalyzerFactory;
import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.TextPage;

import java.util.List;

/**
 * Wraps an existing analyzer, combining the functionality of joining pages with the functionality of joining lines within
 * a page.
 * This effectively turns the entirety of processed contents (all processed pages) into one single-line page to be parsed
 * by the wrapped analyzer.<br>
 * <br>
 * This Analyzer is NOT thread safe.<br>
 * This Analyzer is NOT processing-order independent.<br>
 * This is a decorator.
 */
public final class CrossLineCrossPageAnalyzer implements Analyzer {

    private final Analyzer analyzer;

    /**
     * This constructor is preferred as it allows for a new wrapped analyzer to be created every time {@link #findFeatures()} is
     * called on this wrapper.
     *
     * @param analyzerFactory factory to produce the analyzer to be wrapped, which will see all contents passed to this wrapping analyzer as one
     *                        continuous string, treating it as one single-line page
     */
    public CrossLineCrossPageAnalyzer(AnalyzerFactory analyzerFactory) {
        this.analyzer = new CrossPageAnalyzer(() -> new CrossLineAnalyzer(analyzerFactory.createAnalyzer()));
    }

    /**
     * This is a convenience constructor for use when it is safe to reuse the passed analyzer with potentially different
     * contents belonging to the same page number, or when it is guaranteed that {@link #findFeatures()} will not be called
     * more than once on this wrapping analyzer.
     *
     * @param analyzer an analyzer to be wrapped, which will see all contents passed to this wrapping analyzer as one
     *                 continuous string, treating it as one single-line page
     */
    public CrossLineCrossPageAnalyzer(Analyzer analyzer) {
        this.analyzer = new CrossPageAnalyzer(() -> new CrossLineAnalyzer(analyzer));
    }

    @Override
    public List<Feature> findFeatures() {
        return analyzer.findFeatures();
    }

    @Override
    public void processPage(TextPage page) {
        analyzer.processPage(page);
    }
}
