package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.internal.common.DocxSegment;
import nitpeek.io.docx.render.CompositeRun;

import java.util.Optional;

interface DocxSegmentExtractor {
    Optional<DocxSegment<CompositeRun>> extractSegment();
}
