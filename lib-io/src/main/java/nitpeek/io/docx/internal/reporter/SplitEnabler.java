package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.common.DocxParagraph;
import nitpeek.io.docx.internal.common.PartialParagraph;
import nitpeek.io.docx.render.CompositeRun;
import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.render.SplittableRun;

final class SplitEnabler {
    private final RunSplitEnabler runSplitEnabler;

    SplitEnabler(RunSplitEnabler runSplitEnabler) {
        this.runSplitEnabler = runSplitEnabler;
    }

    public DocxParagraph<SplittableRun> convertParagraph(DocxParagraph<? extends CompositeRun> paragraph, RunRenderer runRenderer) {
        var runs = paragraph.runs();
        var splittableRuns = runs.stream().map(run -> runSplitEnabler.toSplittable(run, runRenderer)).toList();
        return new PartialParagraph<>(splittableRuns);
    }
}