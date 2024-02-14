package nitpeek.io.docx.internal.pagesource;

import org.docx4j.wml.P;

import java.util.List;

final class SimpleContentRenderer implements ContentRenderer {

    private final ParagraphRenderer paragraphRenderer;

    public SimpleContentRenderer(ParagraphRenderer paragraphRenderer) {
        this.paragraphRenderer = paragraphRenderer;
    }

    @Override
    public List<String> renderParagraphs(List<Object> paragraphs) {
        var properParagraphs = JaxbUtil.keepElementsOfType(paragraphs, P.class);
        return properParagraphs.stream().map(paragraphRenderer::render).toList();
    }
}
