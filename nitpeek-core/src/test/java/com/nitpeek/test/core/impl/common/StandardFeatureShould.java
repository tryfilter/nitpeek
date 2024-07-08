package com.nitpeek.test.core.impl.common;

import com.nitpeek.core.impl.common.StandardFeature;
import com.nitpeek.core.impl.translate.helper.DefaultNoTranslationTranslation;
import com.nitpeek.core.api.translate.Translation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.nitpeek.core.impl.translate.CoreTranslationKeys.MISSING_PAGES_FEATURE_DESCRIPTION;
import static com.nitpeek.core.impl.translate.CoreTranslationKeys.MISSING_PAGES_FEATURE_NAME;

final class StandardFeatureShould {


    @Test
    void translateUsingProvidedCustomTranslator() {
        StandardFeature feature = StandardFeature.MISSING_PAGES;

        Translation customTranslation = new DefaultNoTranslationTranslation("dummy");

        Assertions.assertEquals(customTranslation.translate(MISSING_PAGES_FEATURE_NAME.key()), feature.getType().getFeatureId().getName(customTranslation));
        Assertions.assertEquals(customTranslation.translate(MISSING_PAGES_FEATURE_DESCRIPTION.key()), feature.getType().getFeatureId().getDescription(customTranslation));
    }
}
