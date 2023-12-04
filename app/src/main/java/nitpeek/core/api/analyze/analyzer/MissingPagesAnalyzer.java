package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.internal.Confidence;
import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.common.*;
import nitpeek.core.api.common.util.PageRange;
import nitpeek.translation.DefaultEnglishTranslator;
import nitpeek.translation.Translator;

import java.util.*;

/**
 * Reports sections of pages that were expected to be processed but weren't.<br>
 * <br>
 * This analyzer is <i>not</i> thread safe.<br>
 * This analyzer is independent of page processing order, however if pages were processed out of order and
 * {@link #findFeatures()} is called before all pages were processed, then this analyzer may over-eagerly report
 * missing pages that may yet be processed. <br>
 * Note that this has implications also on the behavior of an observable version of this analyzer: if pages
 * are processed out of order, the observable wrapper may send notifications classifying as missing pages that are
 * yet to be processed.
 */
public final class MissingPagesAnalyzer implements Analyzer {

    private final Set<Integer> processedPageNumbers = new HashSet<>();
    private final Translator i18n;

    /**
     * Uses the default english translator for user-facing text
     */
    public MissingPagesAnalyzer() {
        this(new DefaultEnglishTranslator());
    }

    public MissingPagesAnalyzer(Translator i18n) {
        this.i18n = i18n;
    }

    @Override
    public void processPage(TextPage page) {
        processedPageNumbers.add(page.getPageNumber());
    }

    @Override
    public List<Feature> findFeatures() {
        if (processedPageNumbers.isEmpty())
            return List.of(missingPageProblem(null, Confidence.HIGH));

        var sortedPageNumbers = new TreeSet<>(processedPageNumbers);
        List<Feature> features = new ArrayList<>();

        features.addAll(findMissingPagesInBeginning(sortedPageNumbers));
        features.addAll(findMissingPagesInMiddle(sortedPageNumbers));

        return features;
    }

    private List<Feature> findMissingPagesInBeginning(NavigableSet<Integer> sortedPageNumbers) {
        // first page was processed: no missing pages in beginning
        if (sortedPageNumbers.first() == 0) return List.of();

        return List.of(missingPageProblem(missingPageRange(0, sortedPageNumbers.first() - 1), Confidence.MEDIUM));
    }

    private List<Feature> findMissingPagesInMiddle(NavigableSet<Integer> sortedPageNumbers) {
        List<Feature> features = new ArrayList<>();
        List<PageRange> missingSections = getMissingSections(sortedPageNumbers);
        for (var missingSection : missingSections) {
            features.add(missingPageProblem(TextSelection.fullPages(missingSection), Confidence.HIGH));
        }
        return features;
    }

    private TextSelection missingPageRange(int firstMissingPage, int lastMissingPage) {
        return TextSelection.fullPages(new PageRange(firstMissingPage, lastMissingPage));
    }

    private Feature missingPageProblem(TextSelection pages, Confidence confidence) {
        List<FeatureComponent> components = pages == null ? List.of() : List.of(problemComponent(pages));
        return new SimpleFeature(StandardFeature.MISSING_PAGES.getType(), components, confidence.value());
    }

    private FeatureComponent problemComponent(TextSelection missingPages) {
        return new SimpleFeatureComponent(i18n.missingPagesComponentDescription(missingPages.fromInclusive().page(), missingPages.toInclusive().page()), missingPages);
    }

    private List<PageRange> getMissingSections(NavigableSet<Integer> sortedPageNumbers) {
        List<PageRange> missingSections = new ArrayList<>();
        Integer previousPageNumber = null;
        for (Integer pageNumber : sortedPageNumbers) {
            if (previousPageNumber != null && (pageNumber - previousPageNumber) > 1) {
                missingSections.add(new PageRange(previousPageNumber + 1, pageNumber - 1));
            }
            previousPageNumber = pageNumber;
        }
        return missingSections;
    }
}
