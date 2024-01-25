package nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.util.PageRange;
import nitpeek.core.impl.common.SimpleFeature;
import nitpeek.core.impl.common.SimpleFeatureComponent;
import nitpeek.core.impl.common.SimpleFeatureType;
import nitpeek.core.impl.common.StandardFeature;
import nitpeek.core.internal.Confidence;
import nitpeek.core.api.translate.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.ConcurrentSkipListSet;

import static nitpeek.core.api.common.TextSelection.emptyPages;
import static nitpeek.core.impl.translate.CoreTranslationKeys.*;

/**
 * Reports how many pages were processed and which ones.<br>
 * <br>
 * This analyzer is thread safe.<br>
 * This analyzer is strongly processing-order independent.
 */
public final class PageCounter implements Analyzer {

    private final NavigableSet<Integer> processedPageNumbers = new ConcurrentSkipListSet<>();

    @Override
    public void processPage(TextPage page) {
        processedPageNumbers.add(page.getPageNumber());
    }

    /**
     * @return an unmodifiable snapshot
     */
    @Override
    public List<Feature> findFeatures() {
        SimpleFeatureType simpleFeatureType = StandardFeature.PROCESSED_PAGES.getType();

        return List.of(new SimpleFeature(simpleFeatureType, calculateComponents(), Confidence.MAX.value()));
    }

    private List<FeatureComponent> calculateComponents() {
        List<FeatureComponent> details = detailComponents();
        List<FeatureComponent> result = new ArrayList<>(details.size() + 1);
        result.add(summaryComponent());
        result.addAll(details);
        return Collections.unmodifiableList(result);
    }

    private FeatureComponent summaryComponent() {
        int first = processedPageNumbers.isEmpty() ? 0 : processedPageNumbers.first();
        int last = processedPageNumbers.isEmpty() ? 0 : processedPageNumbers.last();

        return new SimpleFeatureComponent(
                i18n -> i18n.translate(PROCESSED_PAGES_COMPONENT_DESCRIPTION.key(), first, last, processedPageNumbers.size()),
                emptyPages(new PageRange(first, last))
        );
    }

    private List<FeatureComponent> detailComponents() {
        if (processedPageNumbers.isEmpty()) return List.of();

        List<PageRange> ranges = contiguousRanges();
        List<FeatureComponent> result = new ArrayList<>(ranges.size());

        for (var range : contiguousRanges()) {
            result.add(new SimpleFeatureComponent(
                            i18n -> translatePageRange(range.firstPage(), range.lastPage(), i18n),
                    emptyPages(range)
            ));
        }

        return result;
    }

    private String translatePageRange(int firstPage, int lastPage, Translation i18n) {
        if (firstPage == lastPage)
            return i18n.translate(PROCESSED_SINGLE_PAGE_COMPONENT_DESCRIPTION_CHUNK.key(), firstPage);

        return i18n.translate(PROCESSED_PAGES_COMPONENT_DESCRIPTION_CHUNK.key(), firstPage, lastPage);
    }

    private List<PageRange> contiguousRanges() {
        var contiguousRanges = new ArrayList<PageRange>();
        List<Integer> inOrder = processedPageNumbers.stream().toList();

        Integer rangeStart = inOrder.getFirst();
        for (int i = 1; i < inOrder.size(); i++) {
            Integer currentPage = inOrder.get(i);
            Integer previousPage = inOrder.get(i - 1);
            if ((currentPage - previousPage) > 1) {
                contiguousRanges.add(new PageRange(rangeStart, inOrder.get(i - 1)));
                rangeStart = currentPage;
            }
        }
        // in the loop we only add a range when there's a gap, so we need to add the last range by hand
        contiguousRanges.add(new PageRange(rangeStart, inOrder.getLast()));

        return contiguousRanges;
    }
}
