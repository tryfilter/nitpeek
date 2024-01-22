package nitpeek.core.api.report;

import nitpeek.core.impl.report.Indent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class IndentShould {
    private static final String SYMBOL = "+";

    private static final String INPUT_TEXT = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit,
            sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
            Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
            Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.
            Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.""";


    @Test
    void leaveInputUnchangedIfIndentAmountIsZero() {
        Indent indent = new Indent(0, SYMBOL);

        assertEquals(INPUT_TEXT, indent.indentContainedLines(INPUT_TEXT));
    }

    @Test
    void leaveInputUnchangedIfSymbolIsEmptyString() {
        Indent indent = new Indent(5, "");

        assertEquals(INPUT_TEXT, indent.indentContainedLines(INPUT_TEXT));
    }

    @Test
    void indentByCustomAmountAndSymbol() {
        int repeat = 2;
        Indent indent = new Indent(repeat, SYMBOL);
        String expectedPrefix = SYMBOL.repeat(repeat);

        assertTrue(indent.indentContainedLines(INPUT_TEXT).lines().allMatch(line -> line.startsWith(expectedPrefix)));
    }
}
