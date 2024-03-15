package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.types.SplittableRun;

interface RunSplitEnabler {
    SplittableRun toSplittable(CompositeRun run, RunRenderer runRenderer);
}