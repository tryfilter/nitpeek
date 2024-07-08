package com.nitpeek.test.core.impl.translate;

import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.core.impl.translate.helper.DefaultNoTranslationTranslation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.nitpeek.core.impl.translate.CoreTranslationKeys.MISSING_PAGES_COMPONENT_DESCRIPTION;

final class DefaultNoTranslationTranslationShould {


    @Test
    void doTranslationWithDefaultMessageWhenMissingFromSubclass() {
        final String name = "Placeholder";
        Translation dummy = new DefaultNoTranslationTranslation(name);

        String expected = "[missing translation of 'missingPagesComponentDescription' for translator Placeholder]";
        String actual = dummy.translate("missingPagesComponentDescription", 0, 0);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void doTranslationWithCustomMessageWhenMissingFromSubclass() {
        final String name = "Placeholder";
        final String prefix = "{ a prefix, where ";
        final String messageBetweenMethodNameAndTranslatorName = " is not an available translation when translating with the ";
        final String suffix = " translator }";

        Translation dummy = new DefaultNoTranslationTranslation(name, prefix, messageBetweenMethodNameAndTranslatorName, suffix);

        String expected = "{ a prefix, where 'com.nitpeek.core.MISSING_PAGES_COMPONENT_DESCRIPTION' is not an available translation when translating with the Placeholder translator }";
        String actual = dummy.translate(MISSING_PAGES_COMPONENT_DESCRIPTION.key(), 0, 0);

        Assertions.assertEquals(expected, actual);
    }
}
