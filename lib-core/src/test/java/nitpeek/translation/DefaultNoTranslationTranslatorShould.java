package nitpeek.translation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class DefaultNoTranslationTranslatorShould {


    @Test
    void doTranslationWithDefaultMessageWhenMissingFromSubclass() {
        final String name = "Placeholder";
        Translator dummy = new DummyTranslatorUsingNoTranslation(name);

        String expected = "[missing translation of 'missingPagesComponentDescription' for translator Placeholder]";
        String actual = dummy.missingPagesComponentDescription(0, 0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void doTranslationWithCustomMessageWhenMissingFromSubclass() {
        final String name = "Placeholder";
        final String prefix = "{ a prefix, where ";
        final String messageBetweenMethodNameAndTranslatorName = " is not an available translation when translating with the ";
        final String suffix = " translator }";

        Translator dummy = new DummyTranslatorUsingNoTranslation(name, prefix, messageBetweenMethodNameAndTranslatorName, suffix);

        String expected = "{ a prefix, where 'missingPagesComponentDescription' is not an available translation when translating with the Placeholder translator }";
        String actual = dummy.missingPagesComponentDescription(0, 0);

        Assertions.assertEquals(expected, actual);
    }
}
