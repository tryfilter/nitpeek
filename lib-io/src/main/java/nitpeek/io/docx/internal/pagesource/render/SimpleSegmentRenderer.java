package nitpeek.io.docx.internal.pagesource.render;

import nitpeek.io.docx.types.DocxSegment;
import nitpeek.io.docx.types.CompositeRun;

import java.util.List;

public final class SimpleSegmentRenderer implements SegmentRenderer {

    private final ParagraphRenderer paragraphRenderer;

    public SimpleSegmentRenderer(ParagraphRenderer paragraphRenderer) {
        this.paragraphRenderer = paragraphRenderer;
    }

    @Override
    public List<String> render(DocxSegment<? extends CompositeRun> segment) {

        return segment.paragraphs().stream().map(paragraphRenderer::render).toList();
    }
}