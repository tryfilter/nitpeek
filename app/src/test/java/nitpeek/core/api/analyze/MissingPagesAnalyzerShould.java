package nitpeek.core.api.analyze;

import nitpeek.core.api.common.*;
import nitpeek.core.api.common.util.PageRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MissingPagesAnalyzerShould {

    private static final double DELTA = 0.0000001;

    private Analyzer analyzer;

    @BeforeEach
    void setup() {
        analyzer = new MissingPagesAnalyzer();
    }

    @Test
    void reportThatNoPagesWereAnalyzedHighConfidence() {
        // no pages processed
        assertSingleProblem(Confidence.HIGH);
    }

    @Test
    void reportNothingIfFirstPageWasAnalyzed() {
        TextPage firstPage = new SimpleTextPage(emptyList(), 0);
        analyzer.processPage(firstPage);
        assertEquals(emptyList(), analyzer.findProblems());
    }

    @Test
    void reportMissingPagesIfFirstPageWasNotAnalyzedMediumConfidence() {
        TextPage secondPage = new SimpleTextPage(emptyList(), 1);
        analyzer.processPage(secondPage);
        assertSingleProblem(Confidence.MEDIUM);
    }

    @Test
    void reportMissingPagesIfSeveralContiguousFirstPagesWereNotAnalyzedMediumConfidence() {
        TextPage page10 = new SimpleTextPage(emptyList(), 10);
        TextPage page11 = new SimpleTextPage(emptyList(), 11);
        TextPage page12 = new SimpleTextPage(emptyList(), 12);
        analyzer.processPage(page10);
        analyzer.processPage(page11);
        analyzer.processPage(page12);
        assertSingleProblem(Confidence.MEDIUM);
    }

    @Test
    void reportMissingPageIfMiddlePageWasNotAnalyzedHighConfidence() {
        TextPage firstPage = new SimpleTextPage(emptyList(), 0);
        TextPage thirdPage = new SimpleTextPage(emptyList(), 2);
        analyzer.processPage(firstPage);
        analyzer.processPage(thirdPage);
        assertSingleProblem(Confidence.HIGH);
    }

    @Test
    void reportEachPortionOfMissingPagesIfMiddlePagesWereNotAnalyzedHighConfidence() {
        TextPage firstPage = new SimpleTextPage(emptyList(), 0);
        TextPage thirdPage = new SimpleTextPage(emptyList(), 2);
        TextPage fourthPage = new SimpleTextPage(emptyList(), 3);
        TextPage sixthPage = new SimpleTextPage(emptyList(), 5);
        analyzer.processPage(firstPage);
        analyzer.processPage(thirdPage);
        analyzer.processPage(fourthPage);
        analyzer.processPage(sixthPage);

        assertMultipleProblems(Confidence.HIGH, 2);
    }

    @Test
    void reportEachPortionOfContiguousMissingPagesIfMiddlePagesWereNotAnalyzedHighConfidence() {
        TextPage page0 = new SimpleTextPage(emptyList(), 0);
        TextPage page4 = new SimpleTextPage(emptyList(), 4);
        TextPage page5 = new SimpleTextPage(emptyList(), 5);
        TextPage page10 = new SimpleTextPage(emptyList(), 10);
        analyzer.processPage(page0);
        analyzer.processPage(page4);
        analyzer.processPage(page5);
        analyzer.processPage(page10);

        assertMultipleProblems(Confidence.HIGH, 2);
    }

    @Test
    void reportMissingFirstPageDetailsInProblemComponents() {
        TextPage secondPage = new SimpleTextPage(emptyList(), 1);

        analyzer.processPage(secondPage);

        Problem onlyProblem = analyzer.findProblems().getFirst();
        assertMissingPages(onlyProblem, pages(0, 0));

    }

    @Test
    void reportMissingPagesDetailsInProblemComponents() {
        TextPage page0 = new SimpleTextPage(emptyList(), 0);
        TextPage page4 = new SimpleTextPage(emptyList(), 4);
        TextPage page5 = new SimpleTextPage(emptyList(), 5);
        TextPage page12 = new SimpleTextPage(emptyList(), 12);
        TextPage page14 = new SimpleTextPage(emptyList(), 14);
        analyzer.processPage(page0);
        analyzer.processPage(page4);
        analyzer.processPage(page5);
        analyzer.processPage(page12);
        analyzer.processPage(page14);

        List<Problem> problems = analyzer.findProblems();
        Problem missingFrom1To3 = problems.get(0); // missing pages between page 0 and page 4
        Problem missingFrom6To11 = problems.get(1); // missing pages between page 5 and page 12
        Problem missing13 = problems.get(2); // missing page between page 12 and page 14

        assertMissingPages(missingFrom1To3, pages(1, 3));
        assertMissingPages(missingFrom6To11, pages(6, 11));
        assertMissingPages(missing13, pages(13, 13));
    }

    private static TextSelection pages(int firstPage, int lastPage) {

        if (firstPage > lastPage)
            throw new IllegalArgumentException("firstPage must be <= than lastPage, but were " + firstPage + ", respectively " + lastPage);
        return new TextSelection(new TextCoordinate(firstPage, 0, 0), new TextCoordinate(lastPage, 0, 0));
    }

    private static void assertMissingPages(Problem problem, TextSelection... coordinates) {

        List<PageRange> expectedCoordinates = Arrays.stream(coordinates).map(PageRange::pageRange).toList();
        List<PageRange> actualCoordinates = problem.getComponents().stream().map(ProblemComponent::getCoordinates).map(PageRange::pageRange).toList();

        assertEquals(expectedCoordinates, actualCoordinates);
    }

    private void assertSingleProblem(Confidence confidence) {
        assertMultipleProblems(confidence, 1);
    }

    private void assertMultipleProblems(Confidence confidence, int problemCount) {
        List<Problem> problems = analyzer.findProblems();
        assertEquals(problemCount, problems.size());

        for (Problem problem : problems) {
            assertEquals(StandardProblem.MISSING_PAGES.getType(), problem.getType());
            assertEquals(confidence.value(), problem.getConfidence(), DELTA);
        }
    }
}
