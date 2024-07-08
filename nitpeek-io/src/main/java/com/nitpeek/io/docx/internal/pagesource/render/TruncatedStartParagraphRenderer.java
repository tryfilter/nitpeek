package com.nitpeek.io.docx.internal.pagesource.render;

import com.nitpeek.io.docx.types.DocxParagraph;
import com.nitpeek.io.docx.types.CompositeRun;


public final class TruncatedStartParagraphRenderer implements ParagraphRenderer {

    private final ParagraphRenderer renderer;
    private final int firstCharacterInFirstRun;

    public TruncatedStartParagraphRenderer(ParagraphRenderer renderer, int firstCharacterInFirstRun) {
        this.renderer = renderer;
        this.firstCharacterInFirstRun = firstCharacterInFirstRun;

    }

    @Override
    public String render(DocxParagraph<? extends CompositeRun> paragraph) {
        return renderer.render(paragraph).substring(firstCharacterInFirstRun);
    }
}
