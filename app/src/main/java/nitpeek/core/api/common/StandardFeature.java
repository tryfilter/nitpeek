package nitpeek.core.api.common;

import nitpeek.translation.DefaultEnglishTranslator;
import nitpeek.translation.Translator;

import java.util.function.Function;

public enum StandardFeature {

    DEBUG_FEATURE(Translator::debugFeatureName, Translator::debugFeatureDescription, FeatureType.Classification.ERROR),
    MISSING_PAGES(Translator::missingPagesFeatureName, Translator::missingPagesFeatureDescription, FeatureType.Classification.WARNING),
    PROCESSED_PAGES(Translator::processedPagesFeatureName, Translator::processedPagesFeatureDescription, FeatureType.Classification.INFO),
    REPLACE_LITERAL(Translator::replaceLiteralFeatureName, Translator::replaceLiteralFeatureDescription, FeatureType.Classification.ERROR),
    REPLACE_REGEX(Translator::replaceRegexFeatureName, Translator::replaceRegexFeatureDescription, FeatureType.Classification.ERROR);


    private final Translator defaultEnglishTranslator = new DefaultEnglishTranslator();

    private final Function<Translator, String> nameSupplier;
    private final Function<Translator, String> descriptionSupplier;

    private final FeatureType.Classification classification;


    StandardFeature(Function<Translator, String> nameSupplier, Function<Translator, String> descriptionSupplier, FeatureType.Classification classification) {
        this.nameSupplier = nameSupplier;
        this.classification = classification;
        this.descriptionSupplier = descriptionSupplier;
    }

    /**
     * @return the FeatureType of this standard feature, with its name and description translated by the standard english translator
     */
    public FeatureType getType() {
        return getType(defaultEnglishTranslator);
    }

    /**
     * @param translator the translator to use as a source for the feature name and feature description
     * @return the FeatureType of this standard feature, with its name and description translated by the provided translator
     */
    public FeatureType getType(Translator translator) {
        return new FeatureType("nitpeek.core.feature." + this.name(), nameSupplier.apply(translator), classification, descriptionSupplier.apply(translator));
    }
}
