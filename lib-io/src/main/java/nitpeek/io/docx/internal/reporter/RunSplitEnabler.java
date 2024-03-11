package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.render.CompositeRun;
import nitpeek.io.docx.render.RunRenderer;
import nitpeek.io.docx.render.SplittableRun;

public interface RunSplitEnabler {
    SplittableRun toSplittable(CompositeRun run, RunRenderer runRenderer);
}