package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.internal.common.DocxSegment;
import nitpeek.io.docx.internal.common.Partitioned;
import nitpeek.io.docx.render.CompositeRun;
import nitpeek.io.docx.render.DocxPage;

public interface SegmentedDocxPage<C extends CompositeRun> extends DocxPage<C>, Partitioned<DocxSegment<C>> {
}