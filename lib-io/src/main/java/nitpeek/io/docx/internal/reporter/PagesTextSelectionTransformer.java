package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.common.TextSelection;
import nitpeek.io.docx.internal.pagesource.DocxSegment;
import nitpeek.io.docx.internal.pagesource.DocxUtil;
import nitpeek.io.docx.internal.pagesource.ParagraphRenderer;
import nitpeek.util.collection.ListEnds;
import nitpeek.util.collection.SafeSublist;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * This is a simplified implementation which assumes that footnotes are at the end of the page, after the body but
 * before the footer.<br>
 * <br>
 * Note that for DOCX documents, cross-page selections are subtle: they only contain the contents of the body of each
 * page, skipping any footnotes and footer/header text in between.<br>
 * While this selection transformer abstracts this fact away (placing in multipage selections the text as it appears in
 * reading order, including any header, footer, or footnote text that entails), highlighting the resulting
 * {@code DocxTextSelection} in an actual DOCX document may be problematic.<br>
 * <br>
 * Note that this transformer is not section-aware, i.e. it doesn't respect multiple header/footer definitions.
 */
final class PagesTextSelectionTransformer implements TextSelectionTransformer {

    private final List<? extends SegmentedDocxPage> pages;
    private final BiFunction<Integer, Integer, ParagraphRenderer> paragraphRendererFactory;

    PagesTextSelectionTransformer(List<? extends SegmentedDocxPage> pages, BiFunction<Integer, Integer, ParagraphRenderer> paragraphRendererFactory) {
        this.pages = pages;
        this.paragraphRendererFactory = paragraphRendererFactory;
    }

    @Override
    public DocxTextSelection transform(TextSelection textSelection) {
        if (pages.isEmpty()) return new DocxTextSelection(List.of(), 0, 0);

        int firstPageIndex = textSelection.fromInclusive().page();
        int lastPageIndex = textSelection.toInclusive().page();

        var pagesSplit = new ListEnds<>(SafeSublist.subList(pages, firstPageIndex, lastPageIndex));
        var firstPage = pagesSplit.first();
        var lastPage = pagesSplit.last();
        var middlePages = pagesSplit.middle();

        var segmentsFirst = computeFirstPageSegments(firstPage, textSelection);
        var segmentsFromFirstPage = segmentsFirst.segments();
        int indexOfFirstCharacter = segmentsFirst.characterIndex();

        var segmentsLast = computeLastPageSegments(lastPage, textSelection);
        var segmentsFromLastPage = segmentsLast.segments();
        int indexOfLastCharacter = segmentsLast.characterIndex();

        var segmentsFromMiddlePages = middlePages
                .stream()
                .map(SegmentedDocxPage::getAllSegments)
                .flatMap(Collection::stream)
                .toList();

        List<DocxSegment> segments = new ArrayList<>(segmentsFromFirstPage);
        segments.addAll(segmentsFromMiddlePages);

        // If the selection only spans a single page, the segments corresponding to the first and last page must be
        // merged appropriately and common segments shall not be duplicated.
        if (firstPageIndex == lastPageIndex) {
            segments = mergeSegments(segments, segmentsFromLastPage);
        }
        // If the selection spans 2 or more pages, only one of the edge conditions is relevant in each case:
        // first page - all segments starting with and including selection start
        // last page - all segments up to and including selection end
        else {
            segments.addAll(segmentsFromLastPage);
        }

        return new DocxTextSelection(segments, indexOfFirstCharacter, indexOfLastCharacter);
    }

    private List<DocxSegment> mergeSegments(List<DocxSegment> existingSegments, List<DocxSegment> lastPageSegments) {
        var result = new ArrayList<DocxSegment>();
        for (var existingSegment : existingSegments) {
            var lastPageSegment = getMatchingSegment(existingSegment, lastPageSegments);
            if (lastPageSegment != null) result.add(merge(existingSegment, lastPageSegment));
        }
        return result;
    }

    private DocxSegment merge(DocxSegment a, DocxSegment b) {
        int indexOfFirstRun = max(a.indexOfFirstRun(), b.indexOfFirstRun());
        int indexOfLastRun = min(a.indexOfLastRun(), b.indexOfLastRun());
        return new DocxSegment(a.paragraphs(), indexOfFirstRun, indexOfLastRun);
    }

    private @Nullable DocxSegment getMatchingSegment(DocxSegment segment, List<DocxSegment> searchSegments) {
        for (var candidateSegment : searchSegments) {
            if (candidateSegment.paragraphs().equals(segment.paragraphs())) return candidateSegment;
        }
        return null;
    }

    private SegmentsWithCharacterIndex computeFirstPageSegments(SegmentedDocxPage firstPage, TextSelection textSelection) {
        int firstParagraphIndex = textSelection.fromInclusive().line();
        List<DocxSegment> segments = new ArrayList<>(firstPage.getSegmentsFrom(firstParagraphIndex));
        if (segments.isEmpty()) return new SegmentsWithCharacterIndex(List.of(), 0);
        var first = recomputeFirstSegment(segments.getFirst(), textSelection.fromInclusive().character());
        segments.set(0, first.segment());
        return new SegmentsWithCharacterIndex(segments, first.characterIndex());
    }

    private SegmentWithCharacterIndex recomputeFirstSegment(DocxSegment segment, int originalCharacterIndex) {
        if (segment.paragraphs().isEmpty()) return new SegmentWithCharacterIndex(segment, originalCharacterIndex);

        int remainingCharacterIndex = originalCharacterIndex;
        var firstParagraph = segment.paragraphs().getFirst();
        int currentRun;
        int lastRun = DocxUtil.getIndexOfLastRun(firstParagraph);
        for (currentRun = segment.indexOfFirstRun(); currentRun <= lastRun; currentRun++) {
            int currentPage = 0;
            int pageCount = pages.size();
            var paragraphRenderer = paragraphRendererFactory.apply(currentPage, pageCount);
            int currentRunLength = paragraphRenderer.renderBetween(currentRun, currentRun, firstParagraph).length();
            if (currentRunLength <= remainingCharacterIndex) remainingCharacterIndex -= currentRunLength;
            else break;
        }

        return new SegmentWithCharacterIndex(new DocxSegment(segment.paragraphs(), currentRun, segment.indexOfLastRun()), remainingCharacterIndex);
    }

    private SegmentsWithCharacterIndex computeLastPageSegments(SegmentedDocxPage lastPage, TextSelection textSelection) {
        int lastParagraphIndex = textSelection.toInclusive().line();
        List<DocxSegment> segments = new ArrayList<>(lastPage.getSegmentsTo(lastParagraphIndex));
        if (segments.isEmpty()) return new SegmentsWithCharacterIndex(List.of(), 0);
        var last = recomputeLastSegment(segments.getLast(), textSelection.toInclusive().character());
        segments.set(segments.size() - 1, last.segment());
        return new SegmentsWithCharacterIndex(segments, last.characterIndex());
    }

    private SegmentWithCharacterIndex recomputeLastSegment(DocxSegment segment, int originalCharacterIndex) {
        if (segment.paragraphs().isEmpty()) return new SegmentWithCharacterIndex(segment, originalCharacterIndex);

        int remainingCharacterIndex = originalCharacterIndex;
        var lastParagraph = segment.paragraphs().getLast();
        int currentRun;
        for (currentRun = segment.indexOfFirstRun(); currentRun <= segment.indexOfLastRun(); currentRun++) {
            int currentPage = pages.size() - 1;
            int pageCount = pages.size();
            var paragraphRenderer = paragraphRendererFactory.apply(currentPage, pageCount);
            int currentRunLength = paragraphRenderer.renderBetween(currentRun, currentRun, lastParagraph).length();

            if (currentRunLength <= remainingCharacterIndex)
                remainingCharacterIndex -= currentRunLength;
            else
                break;
        }

        return new SegmentWithCharacterIndex(new DocxSegment(segment.paragraphs(), segment.indexOfFirstRun(), currentRun), remainingCharacterIndex);
    }

    private record SegmentWithCharacterIndex(DocxSegment segment, int characterIndex) {
    }

    private record SegmentsWithCharacterIndex(List<DocxSegment> segments, int characterIndex) {

    }
}
