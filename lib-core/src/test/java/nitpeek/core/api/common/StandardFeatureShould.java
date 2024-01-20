package nitpeek.core.api.common;

import nitpeek.translation.DefaultNoTranslationTranslation;
import nitpeek.translation.Translation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static nitpeek.translation.InternalTranslationKeys.MISSING_PAGES_FEATURE_DESCRIPTION;
import static nitpeek.translation.InternalTranslationKeys.MISSING_PAGES_FEATURE_NAME;

final class StandardFeatureShould {


    @Test
    void translateUsingProvidedCustomTranslator() {
        StandardFeature feature = StandardFeature.MISSING_PAGES;

        Translation customTranslation = new DefaultNoTranslationTranslation("dummy");

        Assertions.assertEquals(customTranslation.translate(MISSING_PAGES_FEATURE_NAME.key()), feature.getType().getFeatureId().getName(customTranslation));
        Assertions.assertEquals(customTranslation.translate(MISSING_PAGES_FEATURE_DESCRIPTION.key()), feature.getType().getFeatureId().getDescription(customTranslation));
    }
}
