package nitpeek.io.docx.internal.pagesource.render;

import nitpeek.io.docx.types.DocxParagraph;
import nitpeek.io.docx.types.CompositeRun;


public final class TruncatedEndParagraphRenderer implements ParagraphRenderer {

    private final ParagraphRenderer renderer;
    private final int lastCharacterInLastRun;

    public TruncatedEndParagraphRenderer(ParagraphRenderer renderer, int lastCharacterInLastRun) {
        this.renderer = renderer;
        this.lastCharacterInLastRun = lastCharacterInLastRun;

    }

    @Override
    public String render(DocxParagraph<? extends CompositeRun> paragraph) {
        int lastIndex = paragraph.runs().size() - 1;
        var allRunsExceptLast = paragraph.partitionTo(lastIndex - 1);
        var lastRun = paragraph.partitionFrom(lastIndex);
        return renderer.render(allRunsExceptLast) + renderer.render(lastRun).substring(0, lastCharacterInLastRun + 1);
    }
}