package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;
import nitpeek.io.docx.types.DocxSegment;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static nitpeek.io.docx.testutil.DocxTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

final class BodyAndFootnotesPageAdapterShould {
    private final CompositeRun headerRun1 = run("Run numero uno");
    private final CompositeRun bodyRun1 = run("Run numero dos");
    private final CompositeRun footnotesRun1 = run("Run numero tres");
    private final CompositeRun footerRun1 = complexRun("run 4 p1", "run 4 p2");
    private final CompositeRun headerRun2 = complexRun("run 5 p1", "run 5 p2");
    private final CompositeRun bodyRun2 = complexRun("run 6 p1", "run 6 p2");
    private final CompositeRun footnotesRun2 = run("Run #7");
    private final CompositeRun footerRun2 = run("Run #8");

    private final DocxPage<CompositeRun> page1 = createPage(headerRun1, bodyRun1, footnotesRun1, footerRun1);
    private final DocxPage<CompositeRun> page2 = createPage(headerRun2, bodyRun2, footnotesRun2, footerRun2);

    private final List<DocxPage<CompositeRun>> originalPages = List.of(page1, page2);


    private final BiFunction<Integer, Integer, RunRenderer> runRendererFactory =
            (pageIndex, pageCount) -> new SimpleRunRenderer(pageIndex, pageCount, new SimpleArabicNumberRenderer());

    @Test
    void returnFullPages() {
        var pageAdapter = new BodyAndFootnotesPageAdapter(originalPages, new SplitEnabler(new DefaultRunSplitEnabler()), runRendererFactory);
        assertPagesEquivalent(originalPages, pageAdapter.fullPages());
    }

    @Test
    void returnBodyOnlyPages() {
        var pageAdapter = new BodyAndFootnotesPageAdapter(originalPages, new SplitEnabler(new DefaultRunSplitEnabler()), runRendererFactory);
        var bodyOnlyPages = List.of(
                createPage(null, bodyRun1, null, null),
                createPage(null, bodyRun2, null, null)
        );
        assertPagesEquivalent(bodyOnlyPages, pageAdapter.bodyOnlyPages());
    }

    @Test
    void returnFootnotesOnlyPages() {
        var pageAdapter = new BodyAndFootnotesPageAdapter(originalPages, new SplitEnabler(new DefaultRunSplitEnabler()), runRendererFactory);
        var footnotesOnlyPages = List.of(
                createPage(null, null, footnotesRun1, null),
                createPage(null, null, footnotesRun2, null)
        );
        assertPagesEquivalent(footnotesOnlyPages, pageAdapter.footnotesOnlyPages());
    }

    private void assertPagesEquivalent(List<? extends DocxPage<? extends CompositeRun>> pages1, List<? extends DocxPage<? extends CompositeRun>> pages2) {
        assertEquals(pages1.size(), pages2.size());
        for (int i = 0; i < pages1.size(); i++) {
            var runRenderer = new SimpleRunRenderer(i, pages1.size(), new SimpleArabicNumberRenderer());
            var page1 = pages1.get(i);
            var page2 = pages2.get(i);
            assertPagesEqualContents(page1, page2, runRenderer);
        }
    }

    private void assertPagesEqualContents(DocxPage<? extends CompositeRun> p1, DocxPage<? extends CompositeRun> p2, RunRenderer runRenderer) {
        var header1 = p1.getHeader();
        var header2 = p2.getHeader();
        assertSegmentsEqualsContents(header1.orElse(null), header2.orElse(null), runRenderer);

        var body1 = p1.getHeader();
        var body2 = p2.getHeader();
        assertSegmentsEqualsContents(body1.orElse(null), body2.orElse(null), runRenderer);

        assertFootnotesEquivalent(p1.getFootnotes(), p2.getFootnotes(), runRenderer);

        var footer1 = p1.getHeader();
        var footer2 = p2.getHeader();
        assertSegmentsEqualsContents(footer1.orElse(null), footer2.orElse(null), runRenderer);
    }

    private void assertFootnotesEquivalent(Map<Integer, ? extends DocxSegment<? extends CompositeRun>> footnotes1, Map<Integer, ? extends DocxSegment<? extends CompositeRun>> footnotes2, RunRenderer runRenderer) {
        assertEquals(footnotes1.size(), footnotes2.size());
        for (int footnote : footnotes1.keySet()) {
            var footnote1Segment = footnotes1.get(footnote);
            var footnote2Segment = footnotes2.get(footnote);
            assertSegmentsEqualsContents(footnote1Segment, footnote2Segment, runRenderer);
        }
    }

    private void assertSegmentsEqualsContents(@Nullable DocxSegment<? extends CompositeRun> s1, DocxSegment<? extends CompositeRun> s2, RunRenderer runRenderer) {
        if (s1 == s2) return;
        if (s1 == null || s2 == null) fail("Comparing null segment to non-null segment.");
        assertEqualRuns(s1.componentRuns(), s2.componentRuns(), runRenderer);
    }

    private void assertEqualRuns(List<? extends CompositeRun> runs1, List<? extends CompositeRun> runs2, RunRenderer runRenderer) {
        assertEquals(runs1.size(), runs2.size());
        for (int i = 0; i < runs1.size(); i++) {
            var run1Rendered = runRenderer.render(runs1.get(i));
            var run2Rendered = runRenderer.render(runs2.get(i));
            assertEquals(run1Rendered, run2Rendered);
        }
    }
}