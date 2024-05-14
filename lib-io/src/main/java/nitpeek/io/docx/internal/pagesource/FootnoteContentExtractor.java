package nitpeek.io.docx.internal.pagesource;

import nitpeek.core.api.config.standard.footnote.FootnoteContent;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;

import java.util.Map;

public interface FootnoteContentExtractor {
    Map<Integer, FootnoteContent> extractFootnoteContents(DocxPage<? extends CompositeRun> docxPage, int pageIndex);
}