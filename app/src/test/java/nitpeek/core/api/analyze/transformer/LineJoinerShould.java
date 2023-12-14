package nitpeek.core.api.analyze.transformer;

import nitpeek.core.api.analyze.SimpleTextPage;
import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.common.*;
import nitpeek.core.internal.Confidence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class LineJoinerShould {

    private static final TextPage pageOne = new SimpleTextPage(List.of("Line one.", "Line two.", "line three"), 4);
    private static final TextPage pageTwo = new SimpleTextPage(List.of("Another lines that", "Goes over ", "multiple lines..."), 8);
    private static final TextPage pageThree = new SimpleTextPage(List.of("lkj sdlasoibnnnppap dw"), 5);

    private static final String delimiter = "||~~~||";

    private Transformer lineJoiner;


    @BeforeEach
    void setup() {
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
    void transformTextCoordinatesFirstLine() {

        String searchTerm = "one";
        TextPage page = pageOne;

        assertSingleFeature(inSingleLine(page.getPageNumber(), 5, searchTerm), page, searchTerm);
    }

    @Test
    void transformTextCoordinatesMiddleLine() {

        String searchTerm = "wo";
        TextPage page = pageOne;

        assertSingleFeature(new TextCoordinate(page.getPageNumber(), 1, 6).extendToSelection(searchTerm.length()), page, searchTerm);
    }

    @Test
    void transformTextCoordinatesLastLine() {

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
