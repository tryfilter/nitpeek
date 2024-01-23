package nitpeek.core.impl.analyze.transformer;

import nitpeek.core.impl.analyze.transformer.LineJoiner;
import nitpeek.core.impl.analyze.SimpleTextPage;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.util.Transformer;
import nitpeek.core.impl.common.SimpleFeature;
import nitpeek.core.impl.common.SimpleFeatureComponent;
import nitpeek.core.impl.common.StandardFeature;
import nitpeek.core.internal.Confidence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.lang.Math.max;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class LineJoinerShould {

    private static final TextPage pageOne = new SimpleTextPage(List.of("Line one.", "Line two.", "line three"), 4);
    private static final TextPage pageTwo = new SimpleTextPage(List.of("Another lines that", "Goes over ", "multiple lines..."), 8);
    private static final TextPage pageThree = new SimpleTextPage(List.of("lkj sdlasoibnnnppap dw"), 5);

    private static String delimiter;

    private Transformer lineJoiner;


    @BeforeEach
    void setup() {
        delimiter = "||~~~||";
        lineJoiner = new LineJoiner(delimiter);
    }

    @Test
    void transformPageIntoPageWithSingleLineNoNewlines() {

        TextPage expected = new SimpleTextPage(List.of(joinedToSingleLine(pageOne)), pageOne.getPageNumber());
        assertEquals(expected, lineJoiner.transformPage(pageOne));

    }

    @Test
    void transformMultiplePagesIntoPagesEachWithSingleLineNoNewlines() {

        assertEquals(toSingleLinePage(pageOne), lineJoiner.transformPage(pageOne));
        assertEquals(toSingleLinePage(pageTwo), lineJoiner.transformPage(pageTwo));
        assertEquals(toSingleLinePage(pageThree), lineJoiner.transformPage(pageThree));
    }

    @Test
    void transformTextCoordinatesFullFirstLine() {

        String searchTerm = "Line one.";
        TextPage page = pageOne;

        assertSingleFeature(inSingleLine(page.getPageNumber(), 0, searchTerm), page, searchTerm);
    }

    @Test
    void transformTextCoordinatesFullMiddleLine() {

        String searchTerm = "Line two.";
        TextPage page = pageOne;

        assertSingleFeature(new TextCoordinate(page.getPageNumber(), 1, 0).extendToSelection(searchTerm.length()), page, searchTerm);
    }

    @Test
    void transformTextCoordinatesFullLastLine() {

        String searchTerm = "line three";
        TextPage page = pageOne;

        assertSingleFeature(new TextCoordinate(page.getPageNumber(), 2, 0).extendToSelection(searchTerm.length()), page, searchTerm);
    }

    @Test
    void transformTextCoordinatesFullLastTwoLines() {

        String searchTerm = "Line two." + delimiter + "line three";
        TextPage page = pageOne;

        assertSingleFeature(new TextSelection(new TextCoordinate(page.getPageNumber(), 1, 0), new TextCoordinate(page.getPageNumber(), 2, 9)), page, searchTerm);
    }
    @Test
    void transformTextCoordinatesInFirstLine() {

        String searchTerm = "one";
        TextPage page = pageOne;

        assertSingleFeature(inSingleLine(page.getPageNumber(), 5, searchTerm), page, searchTerm);
    }

    @Test
    void transformTextCoordinatesInMiddleLine() {

        String searchTerm = "wo";
        TextPage page = pageOne;

        assertSingleFeature(new TextCoordinate(page.getPageNumber(), 1, 6).extendToSelection(searchTerm.length()), page, searchTerm);
    }

    @Test
    void transformTextCoordinatesInLastLine() {

        String searchTerm = "ne th";
        TextPage page = pageOne;

        assertSingleFeature(new TextCoordinate(page.getPageNumber(), 2, 2).extendToSelection(searchTerm.length()), page, searchTerm);
    }

    @Test
    void transformTextCoordinatesCrossLines() {

        String searchTerm = String.join(delimiter, List.of("o.", "lin"));
        TextPage page = pageOne;

        var expectedSelection = new TextSelection(new TextCoordinate(page.getPageNumber(), 1, 7), new TextCoordinate(page.getPageNumber(), 2, 2));

        assertSingleFeature(expectedSelection, page, searchTerm);
    }


    /**
     * Separate these test methods from the rest: they only apply for non-empty delimiters; the above test cases should work with any delimiter
     */
    @Test
    void throwWhenTextCoordinateStartsInsideDelimiter() {
        setupForThrowTest();

        String searchTerm = delimiter.substring(delimiter.length() / 2) + "Lin";

        assertThrowsOnFeatureInDelimiter(pageOne, searchTerm);
    }

    @Test
    void throwWhenTextCoordinateStartsAtStartOfDelimiter() {
        setupForThrowTest();

        String searchTerm = delimiter + "Lin";

        assertThrowsOnFeatureInDelimiter(pageOne, searchTerm);
    }

    @Test
    void throwWhenTextCoordinateEndsInsideDelimiter() {
        setupForThrowTest();

        String searchTerm = "two." + delimiter.substring(0, max(delimiter.length() / 2, 1));

        assertThrowsOnFeatureInDelimiter(pageOne, searchTerm);
    }

    @Test
    void throwWhenTextCoordinateEndsAtEndOfDelimiter() {

        setupForThrowTest();
        String searchTerm = "two." + delimiter;
        assertThrowsOnFeatureInDelimiter(pageOne, searchTerm);
    }

    private void setupForThrowTest() {
        delimiter = "+"; // non-empty delimiter for testing of coordinates falling inside the delimiter
        lineJoiner = new LineJoiner(delimiter);
    }

    private void assertThrowsOnFeatureInDelimiter(TextPage pageToTransform, String searchTerm) {
        int indexInLine = joinedToSingleLine(pageToTransform).indexOf(searchTerm);
        Feature fromSingleLinePage = syntheticFeature(inSingleLine(pageToTransform.getPageNumber(), indexInLine, searchTerm));
        lineJoiner.transformPage(pageToTransform);
        assertThrows(IllegalArgumentException.class, () -> lineJoiner.transformFeature(fromSingleLinePage));
    }

    private void assertSingleFeature(TextSelection expectedSelection, TextPage pageToTransform, String searchTerm) {
        int indexInLine = joinedToSingleLine(pageToTransform).indexOf(searchTerm);
        Feature fromSingleLinePage = syntheticFeature(inSingleLine(pageToTransform.getPageNumber(), indexInLine, searchTerm));

        lineJoiner.transformPage(pageToTransform); // transforming coordinates for a page only makes sense after having transformed the page itself
        TextSelection actualCoordinates = coordinates(lineJoiner.transformFeature(fromSingleLinePage));
        assertEquals(expectedSelection, actualCoordinates);
    }

    private TextSelection coordinates(Feature feature) {
        return feature.getComponents().getFirst().getCoordinates();
    }

    private String joinedToSingleLine(TextPage page) {
        return String.join(delimiter, page.getLines());
    }

    private TextSelection inSingleLine(int pageIndex, int charIndex, String value) {
        return new TextCoordinate(pageIndex, 0, charIndex).extendToSelection(value.length());
    }

    private Feature syntheticFeature(TextSelection selectionToWrap) {
        return new SimpleFeature(StandardFeature.DEBUG_FEATURE.getType(), List.of(new SimpleFeatureComponent("Synthetic component", selectionToWrap)), Confidence.MIN.value());
    }

    private TextPage toSingleLinePage(TextPage page) {
        return new SimpleTextPage(List.of(String.join(delimiter, page.getLines())), page.getPageNumber());
    }
}
