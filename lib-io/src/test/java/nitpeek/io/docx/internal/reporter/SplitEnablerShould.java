package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.internal.pagesource.SimpleDocxPage;
import nitpeek.io.docx.internal.pagesource.render.*;
import nitpeek.io.docx.types.CompositeRun;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static nitpeek.io.docx.testutil.DocxTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class SplitEnablerShould {

    private final RunRenderer runRenderer = new SimpleRunRenderer(0, 1, new SimpleArabicNumberRenderer());
    private final SplitEnabler splitEnabler = new SplitEnabler(new DefaultRunSplitEnabler());

    private final CompositeRun run1 = run("Run numero uno");
    private final CompositeRun run2 = run("Run numero dos");
    private final CompositeRun run3 = run("Run numero tres");
    private final CompositeRun run4 = complexRun("run 4 p1", "run 4 p2");
    private final CompositeRun run5 = complexRun("run 5 p1", "run 5 p2");
    private final CompositeRun run6 = complexRun("run 6 p1", "run 6 p2");

    @Test
    void preserveContentsOfConvertedParagraph() {
        var paragraph = paragraph(run1, run2, run3, run4, run5, run6);
        var renderer = new SimpleParagraphRenderer(runRenderer);
        var renderedParagraph = renderer.render(paragraph);
        var splittableParagraph = splitEnabler.convertParagraph(paragraph, runRenderer);
        assertEquals(renderedParagraph, renderer.render(splittableParagraph));
    }

    @Test
    void preserveContentsOfConvertedSegment() {
        var p1 = paragraph(run1);
        var p2 = paragraph(run2, run3);
        var p3 = paragraph(run4, run5, run6);
        var segment = segment(List.of(p1, p2, p3));
        var renderer = new SimpleSegmentRenderer(new SimpleParagraphRenderer(runRenderer));
        var renderedSegment = renderer.render(segment);
        var splittableSegment = splitEnabler.convertSegment(segment, runRenderer);
        assertEquals(renderedSegment, renderer.render(splittableSegment));
    }

    @Test
    void preserveContentsOfConvertedPage() {
        var p1 = paragraph(run1);
        var p2 = paragraph(run2);
        var p3 = paragraph(run3);
        var p4 = paragraph(run4);
        var p5 = paragraph(run5);
        var p6 = paragraph(run6);
        var header = segment(p1);
        var body = segment(List.of(p2, p3));
        var footnotes = Map.of(1, segment(p4), 2, segment(p5));
        var footer = segment(List.of(p5, p6));
        var page = new SimpleDocxPage<>(header, body, footnotes, footer);
        var renderer = new DefaultDocxPageRenderer();
        var renderedPage = renderer.renderPages(List.of(page)).getFirst();
        var splittablePage = splitEnabler.convertPage(page, runRenderer);
        assertEquals(renderedPage, renderer.renderPages(List.of(splittablePage)).getFirst());
    }
}