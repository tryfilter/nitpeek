package nitpeek.core.api.common;

public record SimpleFeatureType(String id, String nameTranslationKey, Classification classification, String descriptionTranslationKey) implements FeatureType{

    @Override
    public Identifier getFeatureId() {
        return new SimpleIdentifier(id, nameTranslationKey, descriptionTranslationKey);
    }

    @Override
    public Classification getClassification() {
        return classification;
    }

}
