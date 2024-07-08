package com.nitpeek.core.impl.common;

import com.nitpeek.core.api.common.FeatureType;
import com.nitpeek.core.api.common.Identifier;

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
