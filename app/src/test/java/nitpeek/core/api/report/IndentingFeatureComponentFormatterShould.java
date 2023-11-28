package nitpeek.core.api.report;

import nitpeek.core.api.common.SimpleFeatureComponent;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextSelection;
import nitpeek.translation.DefaultEnglishTranslator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class IndentingFeatureComponentFormatterShould {

    private static final Indent indent = new Indent(1, "+");

    private static final TextSelection TEXT_SELECTION = new TextSelection(new TextCoordinate(0, 0, 0), new TextCoordinate(2, 2, 2));

    private final IndentingFeatureComponentFormatter formatter = new IndentingFeatureComponentFormatter(new DummyTranslator(), indent);

    @Test
    void indentDescriptionAndTextMatchFromCoordinates() {
        String expected = """
                COORDS
                +Description: DESCRIPTION
                +Text Match: MATCH""";
        String actual = formatter.format(new SimpleFeatureComponent("DESCRIPTION", TEXT_SELECTION, "MATCH"));

        assertEquals(expected, actual);
    }

    private static class DummyTranslator extends DefaultEnglishTranslator {

        @Override
        public String foundFeatureComponentCoordinates(TextSelection coordinates) {
            return "COORDS";
        }
    }

}
