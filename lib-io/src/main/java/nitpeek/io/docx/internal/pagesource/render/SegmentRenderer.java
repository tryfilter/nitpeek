package nitpeek.io.docx.internal.pagesource.render;

import nitpeek.io.docx.types.DocxSegment;
import nitpeek.io.docx.types.CompositeRun;

import java.util.List;

interface SegmentRenderer {
    List<String> render(DocxSegment<? extends CompositeRun> segment);
}