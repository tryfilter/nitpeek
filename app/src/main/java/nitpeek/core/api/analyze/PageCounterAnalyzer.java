package nitpeek.core.api.analyze;

import nitpeek.core.api.common.*;
import nitpeek.core.api.common.util.PageRange;
import nitpeek.translation.DefaultEnglishTranslator;
import nitpeek.translation.Translator;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

import static nitpeek.core.api.common.TextSelection.emptyPages;

public final class PageCounterAnalyzer implements Analyzer {

    private final NavigableSet<Integer> processedPageNumbers = new ConcurrentSkipListSet<>();
    private final Translator i18n;

    PageCounterAnalyzer() {
        this(new DefaultEnglishTranslator());
    }

    public PageCounterAnalyzer(Translator i18n) {
        this.i18n = i18n;
    }

    @Override
    public void processPage(TextPage page) {
        processedPageNumbers.add(page.getPageNumber());
    }

    @Override
    public List<Feature> findFeatures() {
        FeatureType featureType = StandardFeature.PROCESSED_PAGES.getType(i18n);


        return List.of(new SimpleFeature(featureType, calculateComponents(), 1.0));
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
                i18n.processedPagesComponentDescription(first, last, processedPageNumbers.size()),
                emptyPages(new PageRange(first, last))
        );
    }

    private List<FeatureComponent> detailComponents() {
        if (processedPageNumbers.isEmpty()) return List.of();

        List<PageRange> ranges = contiguousRanges();
        List<FeatureComponent> result = new ArrayList<>(ranges.size());

        for (var range : contiguousRanges()) {
            result.add(new SimpleFeatureComponent(
                    i18n.processedPagesComponentDescription(range.firstPage(), range.lastPage()),
                    emptyPages(new PageRange(range.firstPage(), range.lastPage()))
            ));
        }


        return result;
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
