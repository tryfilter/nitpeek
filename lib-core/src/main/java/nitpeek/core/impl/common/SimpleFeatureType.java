package nitpeek.core.impl.common;

import nitpeek.core.api.common.FeatureType;
import nitpeek.core.api.common.Identifier;

public record SimpleFeatureType(String id, String nameTranslationKey, Classification classification, String descriptionTranslationKey) implements FeatureType {

    @Override
    public Identifier getFeatureId() {
        return new SimpleIdentifier(id, nameTranslationKey, descriptionTranslationKey);
    }

    @Override
    public Classification getClassification() {
        return classification;
    }

}
