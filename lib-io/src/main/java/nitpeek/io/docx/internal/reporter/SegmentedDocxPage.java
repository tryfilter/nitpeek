package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.types.DocxSegment;
import nitpeek.io.docx.internal.common.Partitioned;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;

public interface SegmentedDocxPage<C extends CompositeRun> extends DocxPage<C>, Partitioned<DocxSegment<C>> {
}