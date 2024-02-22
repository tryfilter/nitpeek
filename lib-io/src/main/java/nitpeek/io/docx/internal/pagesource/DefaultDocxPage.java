package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.internal.reporter.SegmentedDocxPage;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.docx4j.wml.P;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static nitpeek.io.docx.internal.pagesource.DocxUtil.getIndexOfLastRun;

public final class DefaultDocxPage implements DocxPage, SegmentedDocxPage {
    @Nullable private final DocxSegment header;
    private final DocxSegment body;
    private final SortedMap<Integer, DocxSegment> footnotes;
    @Nullable private final DocxSegment footer;

    private final int paragraphsInHeader;
    private final int paragraphsInBody;
    private final List<P> footnotesParagraphs;
    private final int paragraphsInFootnotes;
    private final int paragraphsInFooter;


    public DefaultDocxPage(@Nullable DocxSegment header, DocxSegment body, Map<Integer, DocxSegment> footnotes, @Nullable DocxSegment footer) {
        this.header = header;
        this.body = body;
        this.footnotes = new TreeMap<>(footnotes);
        this.footer = footer;

        this.paragraphsInHeader = this.header == null ? 0 : this.header.paragraphs().size();
        this.paragraphsInBody = this.body.paragraphs().size();
        this.footnotesParagraphs = this.footnotes.values().stream().map(DocxSegment::paragraphs).flatMap(Collection::stream).toList();
        this.paragraphsInFootnotes = this.footnotesParagraphs.size();
        this.paragraphsInFooter = this.footer == null ? 0 : this.footer.paragraphs().size();
    }


    @Override
    public Optional<DocxSegment> getHeader() {
        return Optional.ofNullable(header);
    }

    @Override
    public DocxSegment getBody() {
        return body;
    }

    /**
     * @return an unmodifiable copy
     */
    @Override
    public SortedMap<Integer, DocxSegment> getFootnotes() {
        return Collections.unmodifiableSortedMap(footnotes);
    }

    @Override
    public Optional<DocxSegment> getFooter() {
        return Optional.ofNullable(footer);
    }


    private List<P> paragraphs(@Nullable DocxSegment segment) {
        if (segment == null) return List.of();
        return segment.paragraphs();
    }

    @Override
    public List<DocxSegment> getSegmentsFrom(int firstParagraphIndex) {
        return getSegments(firstParagraphIndex, paragraphCount() - 1);
    }

    @Override
    public List<DocxSegment> getSegmentsTo(int lastParagraphIndex) {
        return getSegments(0, lastParagraphIndex);
    }

    private int paragraphCount() {
        return paragraphsInHeader + paragraphsInBody + footnotesParagraphs.size() + paragraphsInFooter;
    }

    @Override
    public List<DocxSegment> getSegments(int firstParagraphIndex, int lastParagraphIndex) {
        return getAllSegments().subList(firstParagraphIndex, lastParagraphIndex + 1);
    }

    @Override
    public List<DocxSegment> getAllSegments() {

        var allParagraphs = Stream.of(paragraphs(header), body.paragraphs(), footnotesParagraphs, paragraphs(footer))
                .flatMap(Collection::stream).toList();

        return IntStream.range(0, allParagraphs.size()).mapToObj(i -> getSegmentFor(i, allParagraphs)).toList();
    }

    private DocxSegment getSegmentFor(int paragraphIndex, List<P> paragraphs) {
        var singleParagraphInSegment = List.of(paragraphs.get(paragraphIndex));
        var runRange = runsContainedInParagraph(paragraphIndex);
        return new DocxSegment(singleParagraphInSegment, runRange.firstRunIndex(), runRange.lastRunIndex());
    }

    private RunsRange runsContainedInParagraph(int paragraphIndex) {

        var segmentWithIndex = translateToSegmentIndex(paragraphIndex);
        int paragraphInSegment = segmentWithIndex.paragraphIndexWithingSegment();
        DocxSegment segment = segmentWithIndex.segment();

        int firstRun = getIndexOfFirstRunInParagraph(paragraphInSegment, segment);
        int lastRun = getIndexOfLastRunInParagraph(paragraphInSegment, segment);
        return new RunsRange(firstRun, lastRun);
    }

    private int getIndexOfFirstRunInParagraph(int paragraphIndexInSegment, DocxSegment segment) {
        if (isFirstParagraph(paragraphIndexInSegment)) return segment.indexOfFirstRun();

        return 0;
    }

    private int getIndexOfLastRunInParagraph(int paragraphIndexInSegment, DocxSegment segment) {
        if (isLastParagraph(paragraphIndexInSegment, segment)) return segment.indexOfLastRun();

        var paragraph = segment.paragraphs().get(paragraphIndexInSegment);
        return getIndexOfLastRun(paragraph);
    }

    private boolean isFirstParagraph(int paragraphInSegment) {
        return paragraphInSegment == 0;
    }

    private boolean isLastParagraph(int paragraphInSegment, DocxSegment segment) {
        return paragraphInSegment == segment.paragraphs().size() - 1;
    }

    private record RunsRange(int firstRunIndex, int lastRunIndex) {
    }

    private SegmentWithIndex translateToSegmentIndex(int paragraphIndex) {

        int remainingIndex = paragraphIndex;
        if (remainingIndex < paragraphsInHeader) return new SegmentWithIndex(header, remainingIndex);
        remainingIndex -= paragraphsInHeader;

        if (remainingIndex < paragraphsInBody) return new SegmentWithIndex(body, remainingIndex);
        remainingIndex -= paragraphsInBody;

        if (remainingIndex < paragraphsInFootnotes) return getSegmentFromFootnotes(remainingIndex);
        remainingIndex -= paragraphsInFootnotes;

        if (remainingIndex < paragraphsInFooter) return new SegmentWithIndex(footer, remainingIndex);

        throw new IndexOutOfBoundsException("Paragraph index too large: " + paragraphIndex + " (only " + paragraphCount() + " total paragraphs.");
    }

    private SegmentWithIndex getSegmentFromFootnotes(int paragraphIndex) {
        int remainingIndex = paragraphIndex;
        for (var footnote : footnotes.entrySet()) {
            DocxSegment segment = footnote.getValue();
            int paragraphsInFootnote = segment.paragraphs().size();
            if (remainingIndex < paragraphsInFootnote) return new SegmentWithIndex(segment, remainingIndex);
            remainingIndex -= paragraphsInFootnote;
        }
        throw new IndexOutOfBoundsException("Footnotes contain no paragraph of index" + paragraphIndex);
    }

    private record SegmentWithIndex(DocxSegment segment, int paragraphIndexWithingSegment) {
        public SegmentWithIndex {
            if (paragraphIndexWithingSegment < 0)
                throw new IndexOutOfBoundsException("Paragraph index within segment may not be negative. Was " + paragraphIndexWithingSegment);
            int paragraphCount = segment.paragraphs().size();
            if (paragraphIndexWithingSegment >= paragraphCount)
                throw new IndexOutOfBoundsException("Paragraph index within segment may not be >= paragraph count. Was "
                        + paragraphIndexWithingSegment + " but only " + paragraphCount + " total paragraphs.");
        }
    }
}
