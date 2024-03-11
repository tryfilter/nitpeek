package nitpeek.io.docx.internal.common;

import nitpeek.io.docx.render.CompositeRun;

import java.util.Optional;
import java.util.SortedMap;

public interface DocxPage<C extends CompositeRun> {

    Optional<DocxSegment<C>> getHeader();
    DocxSegment<C> getBody();
    SortedMap<Integer, DocxSegment<C>> getFootnotes();
    Optional<DocxSegment<C>> getFooter();
}
