package com.nitpeek.io.docx.internal.pagesource.render;

import com.nitpeek.io.docx.types.DocxParagraph;
import com.nitpeek.io.docx.types.CompositeRun;


public final class TruncatedParagraphRenderer implements ParagraphRenderer {

    private final int firstCharacterInFirstRun;
    private final ParagraphRenderer truncatedEndParagraphRenderer;

    public TruncatedParagraphRenderer(ParagraphRenderer renderer, int firstCharacterInFirstRun, int lastCharacterInLastRun) {
        this.firstCharacterInFirstRun = firstCharacterInFirstRun;
        this.truncatedEndParagraphRenderer = new TruncatedEndParagraphRenderer(renderer, lastCharacterInLastRun);
    }

    @Override
    public String render(DocxParagraph<? extends CompositeRun> paragraph) {
        return truncatedEndParagraphRenderer.render(paragraph).substring(firstCharacterInFirstRun);
    }
}