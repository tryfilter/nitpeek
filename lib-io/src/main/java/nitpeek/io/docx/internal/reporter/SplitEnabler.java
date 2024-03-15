package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.common.*;
import nitpeek.io.docx.types.*;


public final class SplitEnabler {
    private final RunSplitEnabler runSplitEnabler;

    public SplitEnabler(RunSplitEnabler runSplitEnabler) {
        this.runSplitEnabler = runSplitEnabler;
    }

    public DocxParagraph<SplittableRun> convertParagraph(DocxParagraph<? extends CompositeRun> paragraph, RunRenderer runRenderer) {
        var runs = paragraph.runs();
        var splittableRuns = runs.stream().map(run -> runSplitEnabler.toSplittable(run, runRenderer)).toList();
        return new PartialParagraph<>(splittableRuns);
    }
}