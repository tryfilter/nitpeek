package nitpeek.translation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class DefaultEnglishTranslatorShould {

    @Test
    void doEnglishTranslationWhenMissingFromSubclass() {

        Translator dummy = new DummyTranslatorUsingEnglish();

        String expected = "Pages 1-3 have not been processed.";
        String actual = dummy.missingPagesComponentDescription(1, 3);

        Assertions.assertEquals(expected, actual);
    }

}
