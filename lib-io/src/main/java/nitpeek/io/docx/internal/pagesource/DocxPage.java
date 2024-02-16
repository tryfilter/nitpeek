package nitpeek.io.docx.internal.pagesource;

import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

public record DocxPage(
        int pageIndex,
        Optional<DocxSegment> header,
        DocxSegment body,
        SortedMap<Integer, DocxSegment> footnotes,
        Optional<DocxSegment> footer) {
    public DocxPage {
        footnotes = new TreeMap<>(footnotes);
    }

    public DocxPage(int pageIndex,
                    Optional<DocxSegment> header,
                    DocxSegment body,
                    Map<Integer, DocxSegment> footnotes,
                    Optional<DocxSegment> footer) {
        this(pageIndex, header, body, new TreeMap<>(footnotes), footer);
    }
}
