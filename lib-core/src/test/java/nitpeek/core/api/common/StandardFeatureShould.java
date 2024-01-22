package nitpeek.core.api.common;

import nitpeek.core.impl.common.StandardFeature;
import nitpeek.core.impl.translate.helper.DefaultNoTranslationTranslation;
import nitpeek.core.api.translate.Translation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static nitpeek.core.impl.translate.helper.InternalTranslationKeys.MISSING_PAGES_FEATURE_DESCRIPTION;
import static nitpeek.core.impl.translate.helper.InternalTranslationKeys.MISSING_PAGES_FEATURE_NAME;

final class StandardFeatureShould {


    @Test
    void translateUsingProvidedCustomTranslator() {
        StandardFeature feature = StandardFeature.MISSING_PAGES;

        Translation customTranslation = new DefaultNoTranslationTranslation("dummy");

        Assertions.assertEquals(customTranslation.translate(MISSING_PAGES_FEATURE_NAME.key()), feature.getType().getFeatureId().getName(customTranslation));
        Assertions.assertEquals(customTranslation.translate(MISSING_PAGES_FEATURE_DESCRIPTION.key()), feature.getType().getFeatureId().getDescription(customTranslation));
    }
}
