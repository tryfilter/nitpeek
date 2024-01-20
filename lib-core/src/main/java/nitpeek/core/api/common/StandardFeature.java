package nitpeek.core.api.common;

import nitpeek.core.api.common.FeatureType.Classification;
import nitpeek.translation.InternalTranslationKeys;

import static nitpeek.core.api.common.FeatureType.Classification.*;
import static nitpeek.translation.InternalTranslationKeys.*;

public enum StandardFeature {

    DEBUG_FEATURE(DEBUG_FEATURE_NAME, DEBUG_FEATURE_DESCRIPTION, ERROR),
    MISSING_PAGES(MISSING_PAGES_FEATURE_NAME, MISSING_PAGES_FEATURE_DESCRIPTION, WARNING),
    PROCESSED_PAGES(PROCESSED_PAGES_FEATURE_NAME, PROCESSED_PAGES_FEATURE_DESCRIPTION, INFO),
    REPLACE_LITERAL(REPLACE_LITERAL_FEATURE_NAME, REPLACE_LITERAL_FEATURE_DESCRIPTION, ERROR),
    REPLACE_REGEX(REPLACE_REGEX_FEATURE_NAME, REPLACE_REGEX_FEATURE_DESCRIPTION, ERROR),
    UNPAIRED_PARENTHESES(UNPAIRED_PARENTHESES_FEATURE_NAME, UNPAIRED_PARENTHESES_FEATURE_DESCRIPTION, ERROR);


    private final String nameTranslationKey;
    private final String descriptionTranslationKey;

    private final Classification classification;


    StandardFeature(InternalTranslationKeys nameTranslationKey, InternalTranslationKeys descriptionTranslationKey, Classification classification) {
        this.nameTranslationKey = nameTranslationKey.key();
        this.descriptionTranslationKey = descriptionTranslationKey.key();
        this.classification = classification;
    }

    public SimpleFeatureType getType() {
        return new SimpleFeatureType("nitpeek.core.feature." + this.name(), nameTranslationKey, classification, descriptionTranslationKey);
    }
}
