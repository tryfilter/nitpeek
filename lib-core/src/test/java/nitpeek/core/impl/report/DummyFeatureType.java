package nitpeek.core.impl.report;

import nitpeek.core.api.common.FeatureType;
import nitpeek.core.api.common.Identifier;
import nitpeek.core.api.translate.Translation;

public record DummyFeatureType(String id, String featureName, FeatureType.Classification classification,
                        String featureDescription) implements FeatureType {

    @Override
    public Identifier getFeatureId() {
        return new Identifier() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getName(Translation translation) {
                return featureName;
            }

            @Override
            public String getDescription(Translation translation) {
                return featureDescription;
            }
        };
    }

    @Override
    public Classification getClassification() {
        return classification;
    }
}
