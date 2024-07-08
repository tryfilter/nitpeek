package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.io.docx.types.DocxSegment;
import com.nitpeek.io.docx.internal.common.Partitioned;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxPage;

public interface SegmentedDocxPage<C extends CompositeRun> extends DocxPage<C>, Partitioned<DocxSegment<C>> {
}