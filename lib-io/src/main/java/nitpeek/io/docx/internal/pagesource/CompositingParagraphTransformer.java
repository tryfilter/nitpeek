package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.types.DocxParagraph;
import nitpeek.io.docx.internal.common.DocxUtil;
import nitpeek.io.docx.internal.common.PartialParagraph;
import nitpeek.io.docx.types.CompositeRun;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import java.util.List;

final class CompositingParagraphTransformer implements ParagraphTransformer {

    private final RunsCompositor runsCompositor;

    CompositingParagraphTransformer(RunsCompositor runsCompositor) {
        this.runsCompositor = runsCompositor;
    }

    @Override
    public DocxParagraph<CompositeRun> transform(P paragraph) {
        return fromRuns(getRuns(paragraph));
    }

    @Override
    public DocxParagraph<CompositeRun> transformBetween(int firstRunIndex, int lastRunIndex, P paragraph) {
        return fromRuns(getRuns(paragraph).subList(firstRunIndex, lastRunIndex + 1));
    }

    @Override
    public DocxParagraph<CompositeRun> transformFrom(int firstRunIndex, P paragraph) {
        var runs = getRuns(paragraph);
        return fromRuns(runs.subList(firstRunIndex, runs.size()));
    }

    @Override
    public DocxParagraph<CompositeRun> transformTo(int lastRunIndex, P paragraph) {
        var runs = getRuns(paragraph);
        return fromRuns(runs.subList(0, lastRunIndex + 1));
    }


    private PartialParagraph<CompositeRun> fromRuns(List<R> runs) {
        return new PartialParagraph<>(runsCompositor.composit(runs));
    }

    private List<R> getRuns(P paragraph) {
        return DocxUtil.getRuns(paragraph);
    }
}
