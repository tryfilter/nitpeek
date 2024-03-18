package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.pagesource.render.DefaultDocxPageRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.types.CompositeRun;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nitpeek.io.docx.testutil.DocxTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class DocxAnnotatorShould {

    private final CompositeRun run1 = run("Run numero uno");
    private final CompositeRun run2 = run("Run numero dos");
    private final CompositeRun run3 = run("Run numero tres");
    private final CompositeRun run4 = complexRun("run 4 p1", "run 4 p2");
    private final CompositeRun run5 = complexRun("run 5 p1", "run 5 p2");
    private final CompositeRun run6 = complexRun("run 6 p1", "run 6 p2");
    private final CompositeRun run7 = run("Run #7");
    private final CompositeRun run8 = run("Run #8");

    @Test
    void preservePageContentsWhenMakingThemSplittable() {
        var page1 = createPage(run1, run2, run3, run4);
        var page2 = createPage(run5, run6, run7, run8);
        var pages = List.of(page1, page2);
        var renderer = new DefaultDocxPageRenderer();
        var renderedPages = renderer.renderPages(pages);
        var splitEnabler = new SplitEnabler(new DefaultRunSplitEnabler());

        var splittablePages = DocxAnnotator.makePagesSplittable(pages, splitEnabler, (pageIndex, pageCount) -> new SimpleRunRenderer(pageIndex, pageCount, new SimpleArabicNumberRenderer()));
        assertEquals(renderedPages, renderer.renderPages(splittablePages));
    }
}