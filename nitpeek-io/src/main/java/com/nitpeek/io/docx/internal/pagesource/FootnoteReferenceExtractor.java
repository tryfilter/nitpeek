package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxPage;
import com.nitpeek.core.api.config.standard.footnote.FootnoteReference;

import java.util.Set;

public interface FootnoteReferenceExtractor {
    Set<FootnoteReference> extractFootnoteReferences(DocxPage<? extends CompositeRun> docxPage, int pageIndex);
}