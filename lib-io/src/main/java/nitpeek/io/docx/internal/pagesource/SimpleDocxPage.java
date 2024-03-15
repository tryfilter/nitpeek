package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.internal.common.DocxSegment;
import nitpeek.io.docx.render.CompositeRun;
import nitpeek.io.docx.render.DocxPage;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

public final class SimpleDocxPage<C extends CompositeRun> implements DocxPage<C> {
    @Nullable private final DocxSegment<C> header;
    private final DocxSegment<C> body;
    private final SortedMap<Integer, DocxSegment<C>> footnotes;
    @Nullable private final DocxSegment<C> footer;

    public SimpleDocxPage(@Nullable DocxSegment<C> header, DocxSegment<C> body, Map<Integer, DocxSegment<C>> footnotes, @Nullable DocxSegment<C> footer) {
        this.header = header;
        this.body = body;
        this.footnotes = new TreeMap<>(footnotes);
        this.footer = footer;
    }

    @Override
    public Optional<DocxSegment<C>> getHeader() {
        return Optional.ofNullable(header);
    }

    @Override
    public DocxSegment<C> getBody() {
        return body;
    }

    @Override
    public SortedMap<Integer, DocxSegment<C>> getFootnotes() {
        return Collections.unmodifiableSortedMap(footnotes);
    }

    @Override
    public Optional<DocxSegment<C>> getFooter() {
        return Optional.ofNullable(footer);
    }
}