package nitpeek.io.docx.internal.pagesource.render;

import nitpeek.io.docx.types.DocxParagraph;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.internal.common.RunRenderer;

import java.util.stream.Collectors;


public final class SimpleParagraphRenderer implements ParagraphRenderer {

    private final RunRenderer runRenderer;

    public SimpleParagraphRenderer(RunRenderer runRenderer) {

        this.runRenderer = runRenderer;
    }

    @Override
    public String render(DocxParagraph<? extends CompositeRun> paragraph) {
        return paragraph.runs().stream().map(runRenderer::render).collect(Collectors.joining());
    }
}