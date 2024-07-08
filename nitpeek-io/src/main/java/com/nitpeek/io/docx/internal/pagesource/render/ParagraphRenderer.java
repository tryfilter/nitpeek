package com.nitpeek.io.docx.internal.pagesource.render;

import com.nitpeek.io.docx.types.DocxParagraph;
import com.nitpeek.io.docx.types.CompositeRun;

public interface ParagraphRenderer {
    String render(DocxParagraph<? extends CompositeRun> paragraph);
}
