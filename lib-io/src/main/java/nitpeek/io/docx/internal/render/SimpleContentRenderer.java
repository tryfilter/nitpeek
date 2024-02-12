package nitpeek.io.docx.internal.render;

import nitpeek.io.docx.internal.JaxbUtil;
import org.docx4j.wml.P;

import java.util.List;

public final class SimpleContentRenderer implements ContentRenderer {

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
