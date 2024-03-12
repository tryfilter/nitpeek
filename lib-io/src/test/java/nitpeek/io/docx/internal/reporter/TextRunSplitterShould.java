package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.internal.common.SingletonRun;
import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.testutil.DocxTestUtil;
import org.docx4j.wml.*;
import org.junit.jupiter.api.Test;

import static nitpeek.io.docx.testutil.DocxTestUtil.applyStyle;
import static org.docx4j.XmlUtils.marshaltoString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

final class TextRunSplitterShould {

    private final ObjectFactory objectFactory = new ObjectFactory();
    private final RunRenderer runRenderer = new SimpleRunRenderer(0, 0, new SimpleArabicNumberRenderer());

    @Test
    void preserveStyleInResultingRuns() {
        R runToSplit = DocxTestUtil.createSampleRun("Sample text");
        applyStyle(runToSplit);
        String originalStyle = styleAsString(runToSplit);

        var textRunSplitter = new TextRunSplitter();
        var splitResult = textRunSplitter.splitAfter(runToSplit, 4);
        var original = splitResult.remaining();
        var splitOff = splitResult.splitOff().orElse(null);
        assertNotNull(splitOff);

        assertEquals(originalStyle, styleAsString(original));
        assertEquals(originalStyle, styleAsString(splitOff));
    }

    @Test
    void splitTextContent() {
        String originalText = "012345679890";
        int lastIndex = 5;
        R runToSplit = DocxTestUtil.createSampleRun(originalText);

        var textRunSplitter = new TextRunSplitter();
        var splitResult = textRunSplitter.splitAfter(runToSplit, lastIndex);
        var original = splitResult.remaining();
        var splitOff = splitResult.splitOff().orElse(null);
        assertNotNull(splitOff);

        assertEquals(originalText.substring(0, lastIndex + 1), runRenderer.render(new SingletonRun(original)));
        assertEquals(originalText.substring(lastIndex + 1), runRenderer.render(new SingletonRun(splitOff)));
    }

    private String styleAsString(R run) {
        return marshaltoString(run.getRPr());
    }
}