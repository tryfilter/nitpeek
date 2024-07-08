package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import com.nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import com.nitpeek.io.docx.internal.common.SingletonRun;
import com.nitpeek.io.docx.internal.common.RunRenderer;
import com.nitpeek.io.docx.testutil.DocxTestUtil;
import com.nitpeek.io.docx.types.SplittableRun;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

final class DefaultRunSplitEnablerShould {

    private final RunSplitEnabler runSplitEnabler = new DefaultRunSplitEnabler();
    private final RunRenderer runRenderer = new SimpleRunRenderer(0, 0, new SimpleArabicNumberRenderer());

    @Test
    void returnSameSplittableRunForSameSingletonRun() {
        var singletonRun = new SingletonRun(DocxTestUtil.createSampleRun("Sample Text"));
        SplittableRun initialConvertedRun = runSplitEnabler.toSplittable(singletonRun, runRenderer);
        SplittableRun sameRunConvertedAgain = runSplitEnabler.toSplittable(singletonRun, runRenderer);
        assertSame(initialConvertedRun, sameRunConvertedAgain);
    }

    @Test
    void preserveContents() {
        var singletonRun = new SingletonRun(DocxTestUtil.createSampleRun("Sample Text"));
        SplittableRun splittableRun = runSplitEnabler.toSplittable(singletonRun, runRenderer);
        assertEquals(runRenderer.render(singletonRun), runRenderer.render(splittableRun));
    }
}