package nitpeek.io.docx.internal.pagesource;

import java.util.Optional;

interface DocxSegmentExtractor {
    Optional<DocxSegment> extractSegment();
}
