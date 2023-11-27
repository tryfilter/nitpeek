package nitpeek.core.api.analyze;

import nitpeek.core.api.common.*;
import nitpeek.core.api.common.util.PageRange;
import nitpeek.translation.DefaultEnglishTranslator;
import nitpeek.translation.Translator;

import java.util.*;

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
            TextSelection missingPageRange = missingPageRange(missingSection.firstPage(), missingSection.lastPage());
            features.add(missingPageProblem(missingPageRange, Confidence.HIGH));
        }
        return features;
    }

    private TextSelection missingPageRange(int firstMissingPage, int lastMissingPage) {
        TextCoordinate firstMissing = new TextCoordinate(firstMissingPage, 0, 0);
        TextCoordinate lastMissing = new TextCoordinate(lastMissingPage, Integer.MAX_VALUE, Integer.MAX_VALUE);
        return new TextSelection(firstMissing, lastMissing);
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
