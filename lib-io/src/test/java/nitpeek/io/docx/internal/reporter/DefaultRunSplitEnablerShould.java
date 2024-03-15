package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.internal.common.SingletonRun;
import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.types.SplittableRun;
import org.junit.jupiter.api.Test;

import static nitpeek.io.docx.testutil.DocxTestUtil.createSampleRun;
import static org.junit.jupiter.api.Assertions.assertSame;

final class OnePerPageSplitEnablerShould {

    private final RunSplitEnabler runSplitEnabler = new DefaultRunSplitEnabler();
    private final RunRenderer runRenderer = new SimpleRunRenderer(0, 0, new SimpleArabicNumberRenderer());

    @Test
    void returnSameSplittableRunForSameSingletonRun() {
        var singletonRun = new SingletonRun(createSampleRun("Sample Text"));
        SplittableRun initialConvertedRun = runSplitEnabler.toSplittable(singletonRun, runRenderer);
        SplittableRun sameRunConvertedAgain = runSplitEnabler.toSplittable(singletonRun, runRenderer);
        assertSame(initialConvertedRun, sameRunConvertedAgain);
    }
}