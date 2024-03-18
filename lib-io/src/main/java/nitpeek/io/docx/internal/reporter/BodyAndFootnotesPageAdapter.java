package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.internal.pagesource.DefaultDocxPage;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;
import nitpeek.io.docx.types.SplittableRun;

import java.util.List;
import java.util.function.BiFunction;

public final class BodyAndFootnotesPageAdapter {

    private final List<? extends SegmentedDocxPage<SplittableRun>> fullPages;
    private final List<? extends SegmentedDocxPage<SplittableRun>> bodyOnlyPages;
    private final List<? extends SegmentedDocxPage<SplittableRun>> footnotesOnlyPages;

    public BodyAndFootnotesPageAdapter(List<? extends DocxPage<? extends CompositeRun>> fullPages, SplitEnabler splitEnabler, BiFunction<Integer, Integer, RunRenderer> runRendererFactory) {
        this.fullPages = DocxAnnotator.makePagesSplittable(fullPages, splitEnabler, runRendererFactory);
        this.bodyOnlyPages = this.fullPages.stream().map(PageTransformers::keepOnlyBody).map(DefaultDocxPage::new).toList();
        this.footnotesOnlyPages = this.fullPages.stream().map(PageTransformers::keepOnlyFootnotes).map(DefaultDocxPage::new).toList();
    }

    public List<SegmentedDocxPage<SplittableRun>> fullPages() {
        return List.copyOf(fullPages);
    }

    public List<SegmentedDocxPage<SplittableRun>> bodyOnlyPages() {
        return List.copyOf(bodyOnlyPages);
    }

    public List<SegmentedDocxPage<SplittableRun>> footnotesOnlyPages() {
        return List.copyOf(footnotesOnlyPages);
    }
}