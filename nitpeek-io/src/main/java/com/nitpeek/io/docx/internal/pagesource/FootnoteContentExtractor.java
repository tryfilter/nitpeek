package com.nitpeek.io.docx.internal.pagesource;

import com.nitpeek.core.api.config.standard.footnote.FootnoteContent;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxPage;

import java.util.Map;

public interface FootnoteContentExtractor {
    Map<Integer, FootnoteContent> extractFootnoteContents(DocxPage<? extends CompositeRun> docxPage, int pageIndex);
}