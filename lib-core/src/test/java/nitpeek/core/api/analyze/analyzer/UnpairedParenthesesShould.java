package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.SimpleTextPage;
import nitpeek.core.api.common.*;
import nitpeek.core.testutil.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static nitpeek.core.testutil.FeatureAssert.assertEquivalentFeatures;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class UnpairedParenthesesShould {

    Analyzer unpairedParentheses;
    private static final String OPEN = "(";
    private static final String CLOSE = ")";

    @BeforeEach
    void setup() {
        unpairedParentheses = new UnpairedParentheses(OPEN, CLOSE);
    }


    @Test
    void findNoFeaturesWhenAllParenthesesArePaired() {

        var page = new SimpleTextPage(
                List.of(
                        "No parentheses on this line",
                        "A(((BCDE((FGH(I))J)KL(MN)OPQRS)TU))VW(XYZ)",
                        "()",
                        "(((((((((())))))))))",
                        "(3 || 4) + 9 * (3 - (2 + 6 + (-1)) - 7) * (8 - 5)"
                ),
                0);

        unpairedParentheses.processPage(page);

        var expected = List.of();
        assertEquals(expected, unpairedParentheses.findFeatures());
    }

    @ParameterizedTest
    @ValueSource(strings = {OPEN, CLOSE})
    void findSingleUnpairedParenthesis(String presentParenthesis) {

        var page = new SimpleTextPage(List.of(presentParenthesis), 0);
        unpairedParentheses.processPage(page);

        var textLocation = new TextCoordinate(0, 0, 0).extendToSelection(presentParenthesis.length());
        String missingParenthesis = presentParenthesis.equals(OPEN) ? CLOSE : OPEN;
        var expected = List.of(unpairedFeature(textLocation, missingParenthesis, presentParenthesis));

        assertEquivalentFeatures(expected, unpairedParentheses);
    }

    @Test
    void findUnclosedOpenParentheses() {

        var page = new SimpleTextPage(List.of("( ( ( ( () () ) ) ( ("), 0);
        unpairedParentheses.processPage(page);

        var firstOpen = openParenthesisAt(0);
        var secondOpen = openParenthesisAt(2);
        var ninthOpen = openParenthesisAt(18);
        var tenthOpen = openParenthesisAt(20);
        var expected = List.of(firstOpen, secondOpen, ninthOpen, tenthOpen);

        assertEquivalentFeatures(expected, unpairedParentheses);
    }

    @Test
    void findUnopenedCloseParentheses() {

        var page = new SimpleTextPage(List.of(") ) ( ) ( ( ) ( ) ) ) )"), 0);
        unpairedParentheses.processPage(page);

        var firstClose = closeParenthesisAt(0);
        var secondClose = closeParenthesisAt(2);
        var seventhClose = closeParenthesisAt(20);
        var eighthClose = closeParenthesisAt(22);
        var expected = List.of(firstClose, secondClose, seventhClose, eighthClose);

        assertEquivalentFeatures(expected, unpairedParentheses);
    }

    @Test
    void findBoth() {

        var page = new SimpleTextPage(List.of("))(("), 0);
        unpairedParentheses.processPage(page);

        var firstClose = closeParenthesisAt(0);
        var secondClose = closeParenthesisAt(1);
        var firstOpen = openParenthesisAt(2);
        var secondOpen = openParenthesisAt(3);
        var expected = List.of(firstClose, secondClose, firstOpen, secondOpen);

        assertEquivalentFeatures(expected, unpairedParentheses);
    }

    @Test
    void findBothInContext() {

        var page = new SimpleTextPage(List.of("....)....(..)...)..(.......("), 0);
        unpairedParentheses.processPage(page);

        var firstClose = closeParenthesisAt(4);
        var secondClose = closeParenthesisAt(16);
        var firstOpen = openParenthesisAt(19);
        var secondOpen = openParenthesisAt(27);
        var expected = List.of(firstClose, secondClose, firstOpen, secondOpen);

        assertEquivalentFeatures(expected, unpairedParentheses);
    }

    @Test
    void findBothComplexParentheses() {
        String open = "<(=---";
        String close = "---=)>";
        unpairedParentheses = new UnpairedParentheses(open, close);

        var page = new SimpleTextPage(List.of("<(=---Inside Parentheses---=)>---=)><(=---<(=------=)>"), 0);
        unpairedParentheses.processPage(page);

        var secondClose = closeParenthesisAt(30, close);
        var secondOpen = openParenthesisAt(36, open);
        var expected = List.of(secondClose, secondOpen);

        assertEquivalentFeatures(expected, unpairedParentheses);
    }

    @Test
    void findNoFeaturesWhenComplexParenthesesOverlap() {
        String open = "{===o==";
        String close = "==o===}";
        unpairedParentheses = new UnpairedParentheses(open, close);

        var page = new SimpleTextPage(List.of("{===o===}"), 0);
        unpairedParentheses.processPage(page);

        var expected = List.of();

        assertEquals(expected, unpairedParentheses.findFeatures());
    }

    @Test
    void findNoFeaturesForEvenNumberOfIdenticalParentheses() {
        String open = "'";
        String close = "'";
        unpairedParentheses = new UnpairedParentheses(open, close);

        var page = new SimpleTextPage(List.of("'test', 'open', 'something else', '' <- empty 'string'"), 0);
        unpairedParentheses.processPage(page);

        var expected = List.of();

        assertEquals(expected, unpairedParentheses.findFeatures());
    }

    @Test
    void findLastParenthesisForOddNumberOfIdenticalParentheses() {
        String open = "'";
        String close = "'";
        unpairedParentheses = new UnpairedParentheses(open, close);

        var page = new SimpleTextPage(List.of("'test', 'open', 'something else', '' <- empty 'string', let's go"), 0);
        unpairedParentheses.processPage(page);

        var loneApostrophe = openParenthesisAt(59, close);

        var expected = List.of(loneApostrophe);

        assertEquivalentFeatures(expected, unpairedParentheses);
    }

    private Feature openParenthesisAt(int character) {
        return openParenthesisAt(character, OPEN);
    }

    private Feature closeParenthesisAt(int character) {
        return closeParenthesisAt(character, CLOSE);
    }

    private Feature openParenthesisAt(int character, String parenthesis) {
        return unpairedFeature(new TextCoordinate(0, 0, character).extendToSelection(parenthesis.length()), "closing", parenthesis);
    }

    private Feature closeParenthesisAt(int character, String parenthesis) {
        return unpairedFeature(new TextCoordinate(0, 0, character).extendToSelection(parenthesis.length()), "open", parenthesis);
    }

    private Feature unpairedFeature(TextSelection textLocation, String missingParenthesis, String presentParenthesis) {
        return TestUtil.featureFromComponents(StandardFeature.UNPAIRED_PARENTHESES.getType(), new SimpleFeatureComponent(missingParenthesis, textLocation, presentParenthesis));
    }
}
