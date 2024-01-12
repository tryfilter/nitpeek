package nitpeek.core.api.common;

import nitpeek.translation.DefaultEnglishTranslator;
import nitpeek.translation.DefaultNoTranslationTranslator;
import nitpeek.translation.Translator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class StandardFeatureShould {

    @Test
    void translateUsingDefaultEnglishTranslatorIfNoneProvided() {
        StandardFeature feature = StandardFeature.MISSING_PAGES;

        Assertions.assertEquals(new DefaultEnglishTranslator().missingPagesFeatureName(), feature.getType().name());
        Assertions.assertEquals(new DefaultEnglishTranslator().missingPagesFeatureDescription(), feature.getType().description());
    }

    @Test
    void translateUsingProvidedCustomTranslator() {
        StandardFeature feature = StandardFeature.MISSING_PAGES;

        Translator customTranslator = new DefaultNoTranslationTranslator("dummy");

        Assertions.assertEquals(customTranslator.missingPagesFeatureName(), feature.getType(customTranslator).name());
        Assertions.assertEquals(customTranslator.missingPagesFeatureDescription(), feature.getType(customTranslator).description());
    }
}
