package nitpeek.io.docx.render;

import nitpeek.io.docx.internal.common.SingletonRun;
import nitpeek.io.docx.render.HighlightAnnotationRenderer.HighlightColor;
import nitpeek.io.docx.testutil.DocxTestUtil;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Highlight;
import org.docx4j.wml.R;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class HighlightAnnotationRendererShould {

    HighlightColor highlightColor = HighlightColor.BLUE;
    private final AnnotationRenderer highlightAnnotationRenderer = new HighlightAnnotationRenderer(highlightColor);

    @Test
    void preserveExistingFormatting() {

        R runToHighlight = DocxTestUtil.createSampleRun("Sample text");
        DocxTestUtil.applyStyle(runToHighlight);
        BooleanDefaultTrue oldBoldValue = runToHighlight.getRPr().getB();
        BooleanDefaultTrue oldItalicsValue = runToHighlight.getRPr().getI();
        highlightAnnotationRenderer.renderAnnotation(new RenderableAnnotation(getMessage(), List.of(new SingletonRun(runToHighlight))));
        assertEquals(oldBoldValue, runToHighlight.getRPr().getB());
        assertEquals(oldItalicsValue, runToHighlight.getRPr().getI());

    }

    @Test
    void addHighlight() {

        R runToHighlight = DocxTestUtil.createSampleRun("Sample text");
        DocxTestUtil.applyStyle(runToHighlight);
        highlightAnnotationRenderer.renderAnnotation(new RenderableAnnotation(getMessage(), List.of(new SingletonRun(runToHighlight))));

        Highlight highlight = runToHighlight.getRPr().getHighlight();
        assertNotNull(highlight);
        assertEquals(highlightColor.getName(), highlight.getVal());
    }

    private Message getMessage() {
        return new Message("Message Text", "Message author");
    }

}