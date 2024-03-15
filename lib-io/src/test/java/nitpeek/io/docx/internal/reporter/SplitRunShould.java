package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.pagesource.ComplexRun;
import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.internal.common.SingletonRun;
import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.SplittableRun;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nitpeek.io.docx.testutil.DocxTestUtil.createSampleRun;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class SplitRunShould {

    public static final String runText = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private SplittableRun splitRun;
    private P singleRunParagraph;

    private final ObjectFactory objectFactory = new ObjectFactory();

    private final RunRenderer runRenderer = new SimpleRunRenderer(0, 0, new SimpleArabicNumberRenderer());

    @BeforeEach
    void setup() {
        singleRunParagraph = new P();
        R run = createSampleRun(runText);
        singleRunParagraph.getContent().add(run);
        splitRun = new SplitRun(new SingletonRun(run), runRenderer, new TextRunSplitter());
    }

    @Test
    void keepRunsInSyncWhenSplitFrom() {
        splitRun.splitFrom(5);
        assertSameRunsInParagraphAsCompositeRun();
    }

    @Test
    void keepRunsInSyncWhenSplitFromEnd() {
        splitRun.splitFrom(runText.length());
        assertSameRunsInParagraphAsCompositeRun();
    }

    @Test
    void keepRunsInSyncWhenSplitTo() {
        splitRun.splitTo(5);
        assertSameRunsInParagraphAsCompositeRun();
    }

    @Test
    void keepRunsInSyncWhenSplitToStart() {
        splitRun.splitTo(-1);
        assertSameRunsInParagraphAsCompositeRun();
    }

    @Test
    void keepRunsInSyncWhenSplitBetween() {
        splitRun.splitBetween(3, 7);
        assertSameRunsInParagraphAsCompositeRun();
    }

    @Test
    void keepRunsInSyncWhenSplittingSingleChar() {
        splitRun.splitBetween(6, 6);
        assertSameRunsInParagraphAsCompositeRun();
    }

    @Test
    void keepRunsInSyncWhenSplitBeforeStart() {
        splitRun.splitFrom(0);
        assertSameRunsInParagraphAsCompositeRun();
    }

    @Test
    void keepRunsInSyncWhenSplitAfterEnd() {
        splitRun.splitTo(runText.length() - 1);
        assertSameRunsInParagraphAsCompositeRun();
    }

    @Test
    void keepRunsInSyncWhenSplitOutsideBothEnds() {
        splitRun.splitBetween(0, runText.length() - 1);
        assertSameRunsInParagraphAsCompositeRun();
    }

    @Test
    void returnCorrectPortionBetween() {
        assertCorrectSplitResult(3, 5);
    }

    @Test
    void returnCorrectPortionSingleChar() {
        assertCorrectSplitResult(5, 5);
    }

    @Test
    void returnCorrectPortionMultipleSingleCharSelections() {
        assertCorrectSplitResult(5, 5);
        assertCorrectSplitResult(7, 7);
    }

    @Test
    void returnCorrectPortionFirstChar() {
        assertCorrectSplitResult(0, 0);
    }

    @Test
    void returnCorrectPortionLastChar() {
        int lastCharIndex = runText.length() - 1;
        assertCorrectSplitResult(lastCharIndex, lastCharIndex);
    }

    @Test
    void returnCorrectPortionFullSelection() {
        int lastCharIndex = runText.length() - 1;
        assertCorrectSplitResult(0, lastCharIndex);
    }

    @Test
    void returnFullSelectionIfNoSplitOccurredFrom() {
        var result = splitRun.splitFrom(0);
        assertEquals(1, splitRun.componentRuns().size());
        assertEquals(runText, runRenderer.render(result));
    }

    @Test
    void returnFullSelectionIfNoSplitOccurredTo() {
        var result = splitRun.splitTo(runText.length() - 1);
        assertEquals(1, splitRun.componentRuns().size());
        assertEquals(runText, runRenderer.render(result));
    }

    @Test
    void produceEquivalentResultOnRepeatedSplit() {
        assertCorrectSplitResult(3, 6);
        assertCorrectSplitResult(3, 6);
        assertCorrectSplitResult(3, 6);
    }

    @Test
    void produceEquivalentResultOnIntersectingSplits() {
        assertCorrectSplitResult(3, 6);
        assertCorrectSplitResult(4, 8);
        assertCorrectSplitResult(3, 6);
        assertCorrectSplitResult(5, 7);
        assertCorrectSplitResult(4, 4);
        assertCorrectSplitResult(3, 6);
        assertCorrectSplitResult(5, 7);
    }

    @Test
    void produceEquivalentResultOnInterleavedSplits() {
        assertCorrectSplitResult(3, 3);
        assertCorrectSplitResult(1, 2);
        assertCorrectSplitResult(3, 4);
        assertCorrectSplitResult(0, 0);
        assertCorrectSplitResult(5, 5);
        assertCorrectSplitResult(8, 8);
        assertCorrectSplitResult(6, 7);
        assertCorrectSplitResult(5, 6);
    }

    @Test
    void ignoreEmptyRuns() {
        var paragraph = objectFactory.createP();
        var empty1 = objectFactory.createR();
        var empty2 = objectFactory.createR();
        var empty3 = objectFactory.createR();
        var empty4 = objectFactory.createR();
        var empty5 = objectFactory.createR();
        var runWithEmptyText1 = createSampleRun("");
        var runWithEmptyText2 = createSampleRun("");
        var prefix = createSampleRun(runText.substring(0, 5)); // ABCDE
        var middle1 = createSampleRun(runText.substring(5, 6)); // F
        var middle2 = createSampleRun(runText.substring(6, 16)); // GHIJKLMNOP
        var postFix = createSampleRun(runText.substring(16)); // QRSTUVWXYZ
        var runs = List.of(empty1, prefix, empty2, empty3, middle1, runWithEmptyText1, middle2, runWithEmptyText2, empty4, postFix, empty5);
        paragraph.getContent().addAll(runs);
        CompositeRun compositeRun = new ComplexRun(runs);
        var splittableRun = new SplitRun(compositeRun, runRenderer, new TextRunSplitter());
        assertCorrectSplitResult(0, runText.length() - 1, splittableRun);
        assertCorrectSplitResult(5, runText.length() - 5, splittableRun);
        assertCorrectSplitResult(5, 5, splittableRun);
        assertCorrectSplitResult(3, 8, splittableRun);
        assertCorrectSplitResult(2, 15, splittableRun);
        assertCorrectSplitResult(2, 16, splittableRun);
        assertCorrectSplitResult(2, 17, splittableRun);
    }

    private void assertCorrectSplitResult(int firstCharacter, int lastCharacter) {
        assertCorrectSplitResult(firstCharacter, lastCharacter, splitRun);
    }

    private void assertCorrectSplitResult(int firstCharacter, int lastCharacter, SplittableRun run) {
        var split = run.splitBetween(firstCharacter, lastCharacter);
        var expectedTextContent = runText.substring(firstCharacter, lastCharacter + 1);
        assertEquals(expectedTextContent, runRenderer.render(split));
    }

    /**
     * While the number of actual runs may seem like an implementation detail, it is crucial that any newly created
     * runs also be added to the parent paragraph (otherwise the changes will not be reflected in the original DOCX).
     */
    private void assertSameRunsInParagraphAsCompositeRun() {
        var runsComposite = splitRun.componentRuns();
        var runsParagraph = singleRunParagraph.getContent();
        assertEquals(runsComposite, runsParagraph);
    }
}