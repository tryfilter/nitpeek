package nitpeek.io.docx.internal.common;

import nitpeek.io.docx.internal.pagesource.Partitioned;
import nitpeek.io.docx.render.CompositeRun;

public interface SegmentedDocxPage<C extends CompositeRun> extends DocxPage<C>, Partitioned<DocxSegment<C>> {
}