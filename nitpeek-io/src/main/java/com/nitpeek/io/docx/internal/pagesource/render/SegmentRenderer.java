package com.nitpeek.io.docx.internal.pagesource.render;

import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxSegment;

import java.util.List;

interface SegmentRenderer {
    List<String> render(DocxSegment<? extends CompositeRun> segment);
}