package com.nitpeek.test.core.impl.translate;

import com.nitpeek.core.impl.translate.helper.Translator;
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
