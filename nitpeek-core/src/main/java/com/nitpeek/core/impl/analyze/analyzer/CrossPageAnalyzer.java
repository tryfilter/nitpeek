package com.nitpeek.core.impl.analyze.analyzer;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.api.analyze.AnalyzerFactory;
import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.TextCoordinate;
import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.api.util.FeatureTransformer;
import com.nitpeek.core.impl.analyze.SimpleTextPage;
import com.nitpeek.core.impl.analyze.transformer.FeatureCoordinatesTransformer;

import java.util.*;


/**
 * Transparently allows a wrapped analyzer that normally only works inside single pages to cross page boundaries.<br>
 * <br>
 * Wraps an analyzer, passing it a single page to process, corresponding to one or more pages processed by the wrapping
 * analyzer.<br>
 * Note that the constructors of this class receive an AnalyzerFactory rather than simply an Analyzer.
 * This is because every time {@link #findFeatures()} is called on the wrapping analyzer, a new wrapped analyzer should
 * be created to avoid the wrapped analyzer processing a page with the same page number ({@code 0}) but potentially multiple
 * different variations of content (lines).<br>
 * Note that all processed pages are joined together, even if they do not have adjacent page numbers.
 * <br>
 * This Analyzer is NOT thread safe.<br>
 * This Analyzer is NOT processing-order independent.<br>
 * This is a decorator.
 */
public final class CrossPageAnalyzer implements Analyzer {

    private final FeatureTransformer coordinateTransformer = FeatureCoordinatesTransformer.fromCoordinateTransform(this::transformCoordinate);


    private final AnalyzerFactory analyzerFactory;

    private final NavigableSet<TextPage> processedPages = new TreeSet<>(Comparator.comparingInt(TextPage::getPageNumber));

    private final List<String> delimiterLines;


    public CrossPageAnalyzer(AnalyzerFactory analyzerFactory) {
        this(analyzerFactory, List.of());
    }

    public CrossPageAnalyzer(AnalyzerFactory analyzerFactory, List<String> delimiterLines) {
        this.analyzerFactory = analyzerFactory;
        this.delimiterLines = delimiterLines;
    }

    /**
     * @return the features produced by the wrapped analyzer on the single page consisting of the concatenation of the lines
     * of all pages processed so far by the wrapping analyzer; note that this implies that page gaps are ignored
     */
    @Override
    public List<Feature> findFeatures() {
        Analyzer analyzer = analyzerFactory.createAnalyzer();
        analyzer.processPage(joinToSinglePage());
        var features = analyzer.findFeatures();
        return features.stream().map(coordinateTransformer::transform).toList();
    }


    private TextPage joinToSinglePage() {
        List<String> combinedLines = new ArrayList<>(processedPages.stream().mapToInt(page -> page.getLines().size()).sum());
        var addDelimiters = false;
        for (var page : processedPages) {
            if (addDelimiters) combinedLines.addAll(delimiterLines);
            else addDelimiters = true;

            combinedLines.addAll(page.getLines());
        }

        return new SimpleTextPage(combinedLines, 0);
    }

    @Override
    public void processPage(TextPage page) {
        processedPages.add(page);
    }

    private TextCoordinate transformCoordinate(TextCoordinate textCoordinate) {
        if (textCoordinate.page() != 0)
            throw new IllegalArgumentException("Encountered non-zero page when converting feature passed to CrossPageAnalyzer." +
                    " CrossPageAnalyzer expects all features it receives to have text coordinates with a page dimension of 0.");
        if (processedPages.isEmpty())
            throw new IllegalStateException("Cannot transform features when no pages have been processed.");

        int currentLine = textCoordinate.line();

        int currentPage = 0;
        for (var page : processedPages) {
            if (currentLine < 0)
                throw new IllegalArgumentException("Cannot transform features whose components fall inside the delimiter.");

            currentPage = page.getPageNumber();
            if (currentLine >= page.getLines().size()) currentLine -= page.getLines().size() + delimiterLines.size();
            else break;
        }


        return new TextCoordinate(currentPage, currentLine, textCoordinate.character());
    }
}
