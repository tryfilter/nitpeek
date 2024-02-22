package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.pagesource.DocxPage;
import nitpeek.io.docx.internal.pagesource.DocxSegment;

import java.util.List;

public interface SegmentedDocxPage extends DocxPage {
    List<DocxSegment> getAllSegments();

    List<DocxSegment> getSegments(int firstParagraphIndex, int lastParagraphIndex);

    List<DocxSegment> getSegmentsFrom(int firstParagraphIndex);

    List<DocxSegment> getSegmentsTo(int lastParagraphIndex);

}
