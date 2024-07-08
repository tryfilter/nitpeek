package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.io.docx.internal.pagesource.DefaultDocxPage;
import com.nitpeek.io.docx.internal.common.RunRendererFactory;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxPage;
import com.nitpeek.io.docx.types.SplittableRun;

import java.util.List;

public final class BodyAndFootnotesPageAdapter {

    private final List<? extends SegmentedDocxPage<SplittableRun>> fullPages;
    private final List<? extends SegmentedDocxPage<SplittableRun>> bodyOnlyPages;
    private final List<? extends SegmentedDocxPage<SplittableRun>> footnotesOnlyPages;

    public BodyAndFootnotesPageAdapter(List<? extends DocxPage<? extends CompositeRun>> fullPages, SplitEnabler splitEnabler, RunRendererFactory runRendererFactory) {
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