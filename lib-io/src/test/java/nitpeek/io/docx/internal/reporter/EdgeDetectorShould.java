package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.pagesource.render.SimpleArabicNumberRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleRunRenderer;
import nitpeek.io.docx.internal.pagesource.run.SingletonRun;
import nitpeek.io.docx.render.CompositeRun;
import nitpeek.io.docx.render.RunRenderer;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class EdgeDetectorShould {

    private final ObjectFactory objectFactory = new ObjectFactory();

    private final R run1 = runOfText("012");
    private final R run2 = runOfText("345");
    private final R run3 = runOfText("678");

    private final List<CompositeRun> wrappedRuns = List.of(new SingletonRun(run1), new SingletonRun(run2), new SingletonRun(run3));

    EdgeDetector edgeDetector;
    private final RunRenderer runRenderer = new SimpleRunRenderer(0, 0, new SimpleArabicNumberRenderer());

    @BeforeEach
    void setup() {
        edgeDetector = new EdgeDetector();
    }

    @Test
    void throwIfIndexTooLarge() {
        var runs = wrappedRuns;
        assertThrows(IndexOutOfBoundsException.class, () -> edgeDetector.computeSelectionEdge(runs, 9, runRenderer));
    }

    @Test
    void returnEdgeInMiddleOfRun() {
        var edge = edgeDetector.computeSelectionEdge(wrappedRuns, 1, runRenderer);
        int expectedRunIndex = 0;
        int expectedCharIndex = 1;
        assertEquals(expectedRunIndex, edge.runIndex());
        assertEquals(expectedCharIndex, edge.characterIndexWithinRun());
    }

    @Test
    void returnEdgeAtStartOfRun() {
        var edge = edgeDetector.computeSelectionEdge(wrappedRuns, 3, runRenderer);
        int expectedRunIndex = 1;
        int expectedCharIndex = 0;
        assertEquals(expectedRunIndex, edge.runIndex());
        assertEquals(expectedCharIndex, edge.characterIndexWithinRun());
    }

    @Test
    void returnEdgeAtStartOfFirstRun() {
        var edge = edgeDetector.computeSelectionEdge(wrappedRuns, 0, runRenderer);
        int expectedRunIndex = 0;
        int expectedCharIndex = 0;
        assertEquals(expectedRunIndex, edge.runIndex());
        assertEquals(expectedCharIndex, edge.characterIndexWithinRun());
    }
    @Test
    void returnEdgeAtEndOfLastRun() {
        var edge = edgeDetector.computeSelectionEdge(wrappedRuns, 5, runRenderer);
        int expectedRunIndex = 1;
        int expectedCharIndex = 2;
        assertEquals(expectedRunIndex, edge.runIndex());
        assertEquals(expectedCharIndex, edge.characterIndexWithinRun());
    }

    @Test
    void returnEdgeAtEndOfRun() {
        var edge = edgeDetector.computeSelectionEdge(wrappedRuns, 8, runRenderer);
        int expectedRunIndex = 2;
        int expectedCharIndex = 2;
        assertEquals(expectedRunIndex, edge.runIndex());
        assertEquals(expectedCharIndex, edge.characterIndexWithinRun());
    }

    private R runOfText(String text) {
        R run = objectFactory.createR();
        Text textElement = objectFactory.createText();
        textElement.setValue(text);
        run.getContent().add(objectFactory.createRT(textElement));
        return run;
    }
}