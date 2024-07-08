package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.io.docx.types.DocxSegment;
import com.nitpeek.io.docx.types.CompositeRun;

import java.util.Optional;

interface DocxSegmentExtractor {
    Optional<DocxSegment<CompositeRun>> extractSegment();
}
