package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.io.docx.internal.common.SingletonRun;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.internal.common.RunRenderer;
import com.nitpeek.io.docx.types.SplittableRun;

import java.util.HashMap;
import java.util.Map;

/**
 * The run splitting mechanics employed by the {@link DocxReporter} rely heavily on the implementation
 * of {@link SplitRun}. However, a SplitRun can only guarantee its correct
 * functioning if it is the only object modifying the underlying runs and their parent paragraph.<br>
 * Therefore, this SplitEnabler enforces this uniqueness through a table lookup.<br>
 * It is paramount that for each DocxPage only a single {@link DefaultRunSplitEnabler} is used so that the table lookup
 * can track all already transformed runs and avoid duplicating them.<br>
 */
public final class DefaultRunSplitEnabler implements RunSplitEnabler {
    private final Map<CompositeRun, SplittableRun> transformedRuns;

    public DefaultRunSplitEnabler() {
        this.transformedRuns = new HashMap<>();
    }

    @Override
    public SplittableRun toSplittable(CompositeRun run, RunRenderer runRenderer) {
        return switch (run) {
            case SingletonRun r -> transformSplittableRun(r, runRenderer);
            case SplitRun r -> r;
            case AtomicRun r -> r;
            case CompositeRun r -> new AtomicRun(r);
        };
    }

    private SplittableRun transformSplittableRun(CompositeRun splittableRun, RunRenderer runRenderer) {
        return transformedRuns.computeIfAbsent(splittableRun, r -> new SplitRun(r, runRenderer, new TextRunSplitter()));
    }
}
