package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.internal.common.RunRenderer;
import com.nitpeek.io.docx.types.SplittableRun;

interface RunSplitEnabler {
    SplittableRun toSplittable(CompositeRun run, RunRenderer runRenderer);
}