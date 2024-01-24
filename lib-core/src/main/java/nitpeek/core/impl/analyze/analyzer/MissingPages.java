package nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.util.PageRange;
import nitpeek.core.impl.common.SimpleFeature;
import nitpeek.core.impl.common.SimpleFeatureComponent;
import nitpeek.core.impl.common.StandardFeature;
import nitpeek.core.internal.Confidence;
import nitpeek.core.impl.translate.SimpleDefaultEnglishTranslation;
import nitpeek.core.api.translate.Translation;

import java.util.*;

import static nitpeek.core.impl.translate.CoreTranslationKeys.MISSING_PAGES_COMPONENT_DESCRIPTION;

/**
 * Reports sections of pages that were expected to be processed but weren't.<br>
 * <br>
 * This analyzer is NOT thread safe.<br>
 * This analyzer is weakly processing-order independent.
 */
public final class MissingPages implements Analyzer {

    private final Set<Integer> processedPageNumbers = new HashSet<>();


    @Override
    public void processPage(TextPage page) {
        processedPageNumbers.add(page.getPageNumber());
    }

    /**
     * @return an unmodifiable snapshot
     */
    @Override
    public List<Feature> findFeatures() {
        if (processedPageNumbers.isEmpty())
            return List.of(missingPageProblem(null, Confidence.HIGH));

        var sortedPageNumbers = new TreeSet<>(processedPageNumbers);
        List<Feature> features = new ArrayList<>();

        features.addAll(findMissingPagesInBeginning(sortedPageNumbers));
        features.addAll(findMissingPagesInMiddle(sortedPageNumbers));

        return Collections.unmodifiableList(features);
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
        return new SimpleFeatureComponent(
                (Translation i18n) -> i18n.translate(
                        MISSING_PAGES_COMPONENT_DESCRIPTION.key(),
                        missingPages.fromInclusive().page(),
                        missingPages.toInclusive().page()
                ),
                missingPages
        );
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
