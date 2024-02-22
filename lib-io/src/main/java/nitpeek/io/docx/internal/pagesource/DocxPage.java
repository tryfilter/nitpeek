package nitpeek.io.docx.internal.pagesource;

import java.util.Optional;
import java.util.SortedMap;

public interface DocxPage {

    Optional<DocxSegment> getHeader();
    DocxSegment getBody();
    SortedMap<Integer, DocxSegment> getFootnotes();
    Optional<DocxSegment> getFooter();
}
