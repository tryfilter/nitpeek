package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.types.DocxSegment;
import nitpeek.io.docx.types.CompositeRun;

import java.util.Optional;

interface DocxSegmentExtractor {
    Optional<DocxSegment<CompositeRun>> extractSegment();
}
