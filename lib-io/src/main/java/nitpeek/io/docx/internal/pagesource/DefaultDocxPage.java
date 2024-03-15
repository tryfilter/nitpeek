package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.types.DocxParagraph;
import nitpeek.io.docx.types.DocxSegment;
import nitpeek.io.docx.internal.common.SimpleDocxSegment;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;
import nitpeek.io.docx.internal.reporter.SegmentedDocxPage;

import java.util.*;
import java.util.stream.Stream;

public final class DefaultDocxPage<C extends CompositeRun> implements SegmentedDocxPage<C> {
    private final DocxPage<C> backingPage;
    private final DocxSegment<C> allParagraphs;


    public DefaultDocxPage(DocxPage<C> backingPage) {
        this.backingPage = backingPage;
        this.allParagraphs = new SimpleDocxSegment<>(allParagraphs());
    }

    private List<DocxParagraph<C>> allParagraphs() {
        return Stream.of(
                        backingPage.getHeader().stream().toList(),
                        List.of(backingPage.getBody()),
                        backingPage.getFootnotes().values().stream().toList(),
                        backingPage.getFooter().stream().toList()
                )
                .flatMap(Collection::stream)
                .map(DocxSegment::paragraphs)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public Optional<DocxSegment<C>> getHeader() {
        return backingPage.getHeader();
    }

    @Override
    public DocxSegment<C> getBody() {
        return backingPage.getBody();
    }

    @Override
    public SortedMap<Integer, DocxSegment<C>> getFootnotes() {
        return backingPage.getFootnotes();
    }

    @Override
    public Optional<DocxSegment<C>> getFooter() {
        return backingPage.getFooter();
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