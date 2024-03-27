package nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.analyze.transformer.ComponentTransformer;
import nitpeek.core.impl.analyze.transformer.OnlyFeatureTransformer;
import nitpeek.core.impl.common.SimpleFeatureComponent;

import java.util.List;
import java.util.function.BiFunction;

public final class CustomDescriptionAnalyzer implements Analyzer {
    private final Analyzer analyzer;
    private final BiFunction<FeatureComponent, Translation, String> descriptionGenerator;

    public CustomDescriptionAnalyzer(Analyzer analyzer, BiFunction<FeatureComponent, Translation, String> descriptionGenerator) {
        this.descriptionGenerator = descriptionGenerator;
        this.analyzer = new TransformingAnalyzer(analyzer, new OnlyFeatureTransformer(new ComponentTransformer(this::withCustomDescription)));
    }

    private FeatureComponent withCustomDescription(FeatureComponent original) {
        return new SimpleFeatureComponent(
                i18n -> descriptionGenerator.apply(original, i18n),
                original.getCoordinates(),
                original.getRelevantTextPortion().orElse(null)
        );
    }

    @Override
    public List<Feature> findFeatures() {
        return analyzer.findFeatures();
    }

    @Override
    public void processPage(TextPage page) {
        analyzer.processPage(page);
    }
}