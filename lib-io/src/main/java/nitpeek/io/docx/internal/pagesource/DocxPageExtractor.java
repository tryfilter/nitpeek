package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.internal.reporter.SegmentedDocxPage;

import java.util.List;

public interface DocxPageExtractor {
    List<SegmentedDocxPage> extractPages();
}
