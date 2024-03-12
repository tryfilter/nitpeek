package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.internal.common.DocxParagraph;
import nitpeek.io.docx.internal.common.DocxSegment;
import nitpeek.io.docx.internal.common.SimpleDocxSegment;
import nitpeek.io.docx.render.CompositeRun;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.stream.Stream;

final class DefaultDocxPage<C extends CompositeRun> implements SegmentedDocxPage<C> {
    @Nullable private final DocxSegment<C> header;
    private final DocxSegment<C> body;
    private final SortedMap<Integer, DocxSegment<C>> footnotes;
    @Nullable private final DocxSegment<C> footer;

    private final List<DocxSegment<C>> footnotesSegments;

    private final DocxSegment<C> allParagraphs;


    public DefaultDocxPage(@Nullable DocxSegment<C> header, DocxSegment<C> body, Map<Integer, ? extends DocxSegment<C>> footnotes, @Nullable DocxSegment<C> footer) {
        this.header = header;
        this.body = body;
        this.footnotes = new TreeMap<>(footnotes);
        this.footer = footer;

        this.footnotesSegments = this.footnotes.values().stream().toList();
        this.allParagraphs = new SimpleDocxSegment<>(allParagraphs());
    }

    private List<DocxParagraph<C>> allParagraphs() {
        return Stream.of(
                        Stream.ofNullable(header).toList(),
                        List.of(body),
                        footnotesSegments,
                        Stream.ofNullable(footer).toList()
                )
                .flatMap(Collection::stream)
                .map(DocxSegment::paragraphs)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public Optional<DocxSegment<C>> getHeader() {
        return Optional.ofNullable(header);
    }

    @Override
    public DocxSegment<C> getBody() {
        return body;
    }

    /**
     * @return an unmodifiable copy
     */
    @Override
    public SortedMap<Integer, DocxSegment<C>> getFootnotes() {
        return Collections.unmodifiableSortedMap(footnotes);
    }

    @Override
    public Optional<DocxSegment<C>> getFooter() {
        return Optional.ofNullable(footer);
    }

    @Override
    public DocxSegment<C> fullPartition() {
        return allParagraphs.fullPartition();
    }

    @Override
    public DocxSegment<C> partitionFrom(int firstIndex) {
        return allParagraphs.partitionFrom(firstIndex);
    }

    @Override
    public DocxSegment<C> partitionTo(int lastIndex) {
        return allParagraphs.partitionTo(lastIndex);
    }

    @Override
    public DocxSegment<C> partitionBetween(int firstIndex, int lastIndex) {
        return allParagraphs.partitionBetween(firstIndex, lastIndex);
    }
}
