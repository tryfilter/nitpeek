package nitpeek.core.impl.translate;

import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.api.util.PageRange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;

final class DefaultFallbackEnglishTranslationShould {

    private static final Translation simpleEnglish = new DefaultFallbackEnglishTranslation();

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void translateExistingKeys(int objectCount) {
        for (var key : CoreTranslationKeys.values()) {
            assertFalse(simpleEnglish.translate(key.key(), objects(objectCount)).isBlank());
        }
    }

    private Object[] objects(int objectCount) {

        // This is a bit hacky and also brittle:
        // we currently only have String & int arguments to use in translations. Calling .toString() for the strings and
        // casting to (Integer) for the ints allows us to pass Integers for all parameters without getting
        // a ClassCastException. Arguably casting to String explicitly for the Strings would be more appropriate and,
        // more importantly, as soon as we accept any new types these tests will break.
        Object[] result = new Integer[objectCount];
        int arbitraryValue = 8;
        Arrays.fill(result, arbitraryValue);

        return result;
    }

    @Test
    void fallbackForMissingKeys() {
        var nonExistentKey = "This key is not explicitly translated";
        assertFalse(simpleEnglish.translate(nonExistentKey).isBlank());
    }

    @Test
    void translateTextSelection() {
        var textSelection = TextSelection.fullPages(new PageRange(1, 3));
        var translated = simpleEnglish.translate(textSelection);
        assertFalse(translated.isBlank());
    }
}