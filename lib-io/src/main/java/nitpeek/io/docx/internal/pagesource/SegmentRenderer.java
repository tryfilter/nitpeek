package nitpeek.io.docx.internal.pagesource;

import java.util.List;

interface SegmentRenderer {
    List<String> render(DocxSegment segment);
}
