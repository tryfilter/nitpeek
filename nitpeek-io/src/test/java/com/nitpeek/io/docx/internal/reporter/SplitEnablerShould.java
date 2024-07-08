package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.io.docx.internal.common.RunRenderer;
import com.nitpeek.io.docx.internal.pagesource.SimpleDocxPage;
import com.nitpeek.io.docx.internal.pagesource.render.*;
import com.nitpeek.io.docx.testutil.DocxTestUtil;
import com.nitpeek.io.docx.types.CompositeRun;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class SplitEnablerShould {

    private final RunRenderer runRenderer = new SimpleRunRenderer(0, 1, new SimpleArabicNumberRenderer());
    private final SplitEnabler splitEnabler = new SplitEnabler(new DefaultRunSplitEnabler());

    private final CompositeRun run1 = DocxTestUtil.run("Run numero uno");
    private final CompositeRun run2 = DocxTestUtil.run("Run numero dos");
    private final CompositeRun run3 = DocxTestUtil.run("Run numero tres");
    private final CompositeRun run4 = DocxTestUtil.complexRun("run 4 p1", "run 4 p2");
    private final CompositeRun run5 = DocxTestUtil.complexRun("run 5 p1", "run 5 p2");
    private final CompositeRun run6 = DocxTestUtil.complexRun("run 6 p1", "run 6 p2");

    @Test
    void preserveContentsOfConvertedParagraph() {
        var paragraph = DocxTestUtil.paragraph(run1, run2, run3, run4, run5, run6);
        var renderer = new SimpleParagraphRenderer(runRenderer);
        var renderedParagraph = renderer.render(paragraph);
        var splittableParagraph = splitEnabler.convertParagraph(paragraph, runRenderer);
        assertEquals(renderedParagraph, renderer.render(splittableParagraph));
    }

    @Test
    void preserveContentsOfConvertedSegment() {
        var p1 = DocxTestUtil.paragraph(run1);
        var p2 = DocxTestUtil.paragraph(run2, run3);
        var p3 = DocxTestUtil.paragraph(run4, run5, run6);
        var segment = DocxTestUtil.segment(List.of(p1, p2, p3));
        var renderer = new SimpleSegmentRenderer(new SimpleParagraphRenderer(runRenderer));
        var renderedSegment = renderer.render(segment);
        var splittableSegment = splitEnabler.convertSegment(segment, runRenderer);
        assertEquals(renderedSegment, renderer.render(splittableSegment));
    }

    @Test
    void preserveContentsOfConvertedPage() {
        var p1 = DocxTestUtil.paragraph(run1);
        var p2 = DocxTestUtil.paragraph(run2);
        var p3 = DocxTestUtil.paragraph(run3);
        var p4 = DocxTestUtil.paragraph(run4);
        var p5 = DocxTestUtil.paragraph(run5);
        var p6 = DocxTestUtil.paragraph(run6);
        var header = DocxTestUtil.segment(p1);
        var body = DocxTestUtil.segment(List.of(p2, p3));
        var footnotes = Map.of(1, DocxTestUtil.segment(p4), 2, DocxTestUtil.segment(p5));
        var footer = DocxTestUtil.segment(List.of(p5, p6));
        var page = new SimpleDocxPage<>(header, body, footnotes, footer);
        var renderer = new DefaultDocxPageRenderer();
        var renderedPage = renderer.renderPages(List.of(page)).getFirst();
        var splittablePage = splitEnabler.convertPage(page, runRenderer);
        assertEquals(renderedPage, renderer.renderPages(List.of(splittablePage)).getFirst());
    }
}