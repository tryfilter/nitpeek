package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.io.docx.internal.pagesource.render.DefaultDocxPageRenderer;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import com.nitpeek.io.docx.testutil.DocxTestUtil;
import com.nitpeek.io.docx.types.CompositeRun;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class DocxAnnotatorShould {

    private final CompositeRun run1 = DocxTestUtil.run("Run numero uno");
    private final CompositeRun run2 = DocxTestUtil.run("Run numero dos");
    private final CompositeRun run3 = DocxTestUtil.run("Run numero tres");
    private final CompositeRun run4 = DocxTestUtil.complexRun("run 4 p1", "run 4 p2");
    private final CompositeRun run5 = DocxTestUtil.complexRun("run 5 p1", "run 5 p2");
    private final CompositeRun run6 = DocxTestUtil.complexRun("run 6 p1", "run 6 p2");
    private final CompositeRun run7 = DocxTestUtil.run("Run #7");
    private final CompositeRun run8 = DocxTestUtil.run("Run #8");

    @Test
    void preservePageContentsWhenMakingThemSplittable() {
        var page1 = DocxTestUtil.createPage(run1, run2, run3, run4);
        var page2 = DocxTestUtil.createPage(run5, run6, run7, run8);
        var pages = List.of(page1, page2);
        var renderer = new DefaultDocxPageRenderer();
        var renderedPages = renderer.renderPages(pages);
        var splitEnabler = new SplitEnabler(new DefaultRunSplitEnabler());

        var splittablePages = DocxAnnotator.makePagesSplittable(pages, splitEnabler, (pageIndex, pageCount) -> new SimpleRunRenderer(pageIndex, pageCount, new SimpleArabicNumberRenderer()));
        assertEquals(renderedPages, renderer.renderPages(splittablePages));
    }
}