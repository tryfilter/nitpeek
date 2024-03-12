package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.render.CompositeRun;

import java.util.List;

public interface DocxPageExtractor {
    List<SegmentedDocxPage<CompositeRun>> extractPages();
}
