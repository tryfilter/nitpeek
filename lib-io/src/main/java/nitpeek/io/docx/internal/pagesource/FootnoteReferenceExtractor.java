package nitpeek.io.docx.internal.pagesource;

import nitpeek.core.api.config.standard.footnote.FootnoteReference;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;

import java.util.Set;

public interface FootnoteReferenceExtractor {
    Set<FootnoteReference> extractFootnoteReferences(DocxPage<? extends CompositeRun> docxPage, int pageIndex);
}