package nitpeek.io.docx.internal.pagesource.render;

import nitpeek.io.docx.internal.common.DocxSegment;
import nitpeek.io.docx.render.CompositeRun;

import java.util.List;

interface SegmentRenderer {
    List<String> render(DocxSegment<? extends CompositeRun> segment);
}