package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.AnalyzerFactory;
import nitpeek.core.api.analyze.SimpleTextPage;
import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.analyze.transformer.FeatureCoordinatesTransformer;
import nitpeek.core.api.analyze.transformer.FeatureTransformer;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.TextCoordinate;

import java.util.*;


/**
 * Wraps an analyzer, passing it a single page to process, corresponding to one or more pages processed by the wrapping
 * analyzer. Note that the constructors of this class receive an AnalyzerFactory rather than simply an Analyzer.
 * This is because every time {@link #findFeatures()} is called on the wrapping analyzer, a new wrapped analyzer should
 * be created to avoid the wrapped analyzer processing a page with the same page number ({@code 0}) but potentially multiple
 * different variations of content (lines).<br>
 * The provided factory should only return the same Analyzer instance on repeated calls if such an instance is capable of
 * adequately processing the same page with different content.
 */
public final class PageJoiner implements Analyzer {

    private final FeatureTransformer coordinateTransformer = FeatureCoordinatesTransformer.fromCoordinateTransform(this::transformCoordinate);


    private final AnalyzerFactory analyzerFactory;

    private final NavigableSet<TextPage> processedPages = new TreeSet<>(Comparator.comparingInt(TextPage::getPageNumber));

    private final List<String> delimiterLines;


    public PageJoiner(AnalyzerFactory analyzerFactory) {
        this(analyzerFactory, List.of());
    }

    public PageJoiner(AnalyzerFactory analyzerFactory, List<String> delimiterLines) {
        this.analyzerFactory = analyzerFactory;
        this.delimiterLines = delimiterLines;
    }

    /**
     *
     * @return the features produced by the wrapped analyzer on the single page consisting of the concatenation the lines
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
            throw new IllegalArgumentException("Encountered non-zero page when converting feature passed to PageJoiner." +
                    " PageJoiner expects all features it receives to have text coordinates with a page dimension of 0.");
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
