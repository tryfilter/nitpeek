package test.nitpeek.core.impl.report;

import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.report.TextRangeDescription;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.report.FancyTextRangeDescription;
import nitpeek.core.impl.translate.DefaultFallbackEnglishTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class FancyTextRangeDescriptionShould {

    private static final Translation i18n = new DefaultFallbackEnglishTranslation();
    private TextRangeDescription description;

    private static final int BASE_PAGE = 3;
    private static final int BASE_LINE = 42;
    private static final int BASE_CHAR = 8;

    private static final int OTHER_PAGE = 6;
    private static final int OTHER_LINE = 55;
    private static final int OTHER_CHAR = 19;

    private static final TextCoordinate base = new TextCoordinate(BASE_PAGE, BASE_LINE, BASE_CHAR);
    private static final TextCoordinate sameEverything = new TextCoordinate(BASE_PAGE, BASE_LINE, BASE_CHAR);
    private static final TextCoordinate differingEverything = new TextCoordinate(OTHER_PAGE, OTHER_LINE, OTHER_CHAR);
    private static final TextCoordinate differingPage = new TextCoordinate(OTHER_PAGE, BASE_LINE, BASE_CHAR);
    private static final TextCoordinate differingLine = new TextCoordinate(BASE_PAGE, OTHER_LINE, BASE_CHAR);
    private static final TextCoordinate differingCharacter = new TextCoordinate(BASE_PAGE, BASE_LINE, OTHER_CHAR);

    private static final TextCoordinate samePage = new TextCoordinate(BASE_PAGE, OTHER_LINE, OTHER_CHAR);
    private static final TextCoordinate sameLine = new TextCoordinate(OTHER_PAGE, BASE_LINE, OTHER_CHAR);
    private static final TextCoordinate sameCharacter = new TextCoordinate(OTHER_PAGE, OTHER_LINE, BASE_CHAR);

    @BeforeEach
    void setup() {
        description = new FancyTextRangeDescription(i18n);
    }


    @Test
    void describePinpointTextCoordinate() {
        TextSelection range = new TextSelection(base, sameEverything);
        String expected = "at page " + BASE_PAGE + " at line " + BASE_LINE + " at character " + BASE_CHAR;
        String actual = description.describe(range);

        assertEquals(expected, actual);
    }

    @Test
    void describeFullRangeTextCoordinate() {
        TextSelection range = new TextSelection(base, differingEverything);
        String expected = "from page " + BASE_PAGE + ", line " + BASE_LINE + ", character " + BASE_CHAR + " to page " + OTHER_PAGE + ", line " + OTHER_LINE + ", character " + OTHER_CHAR;
        String actual = description.describe(range);

        assertEquals(expected, actual);
    }

    @Test
    void describeSamePageTextCoordinate() {
        TextSelection range = new TextSelection(base, samePage);
        String expected = "at page " + BASE_PAGE + " from line " + BASE_LINE + ", character " + BASE_CHAR + " to line " + OTHER_LINE + ", character " + OTHER_CHAR;
        String actual = description.describe(range);

        assertEquals(expected, actual);
    }


    @Test
    void describeSameLineTextCoordinate() {
        TextSelection range = new TextSelection(base, sameLine);
        String expected = "from page " + BASE_PAGE + ", line " + BASE_LINE + ", character " + BASE_CHAR + " to page " + OTHER_PAGE + ", line " + BASE_LINE + ", character " + OTHER_CHAR;
        String actual = description.describe(range);

        assertEquals(expected, actual);
    }

    @Test
    void describeSameCharacterTextCoordinate() {
        TextSelection range = new TextSelection(base, sameCharacter);
        String expected = "from page " + BASE_PAGE + ", line " + BASE_LINE + ", character " + BASE_CHAR + " to page " + OTHER_PAGE + ", line " + OTHER_LINE + ", character " + BASE_CHAR;
        String actual = description.describe(range);

        assertEquals(expected, actual);
    }

    @Test
    void describeSamePageAndLineTextCoordinate() {
        TextSelection range = new TextSelection(base, differingCharacter);
        String expected = "at page " + BASE_PAGE + " at line " + BASE_LINE + " from character " + BASE_CHAR + " to character " + OTHER_CHAR;
        String actual = description.describe(range);

        assertEquals(expected, actual);
    }

    @Test
    void describeSamePageAndCharacterTextCoordinate() {
        TextSelection range = new TextSelection(base, differingLine);
        String expected = "at page " + BASE_PAGE + " from line " + BASE_LINE + ", character " + BASE_CHAR + " to line " + OTHER_LINE + ", character " + BASE_CHAR;
        String actual = description.describe(range);

        assertEquals(expected, actual);
    }

    @Test
    void describeSameLineAndCharacterTextCoordinate() {
        TextSelection range = new TextSelection(base, differingPage);
        String expected = "from page " + BASE_PAGE + ", line " + BASE_LINE + ", character " + BASE_CHAR + " to page " + OTHER_PAGE + ", line " + BASE_LINE + ", character " + BASE_CHAR;
        String actual = description.describe(range);

        assertEquals(expected, actual);
    }
}
