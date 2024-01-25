package nitpeek.core.impl.report;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.report.FeatureComponentFormatter;
import nitpeek.core.api.report.FeatureFormatter;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.translate.DefaultFallbackEnglishTranslation;

import static nitpeek.core.impl.translate.CoreTranslationKeys.DESCRIPTION;
import static nitpeek.core.impl.translate.CoreTranslationKeys.FOUND_FEATURE_NAME;

public final class IndentingFeatureFormatter implements FeatureFormatter {


    private final FeatureComponentFormatter componentFormatter;
    private final Indent indent;
    private final Translation i18n;


    public IndentingFeatureFormatter() {
        this(new DefaultFallbackEnglishTranslation());
    }


    public IndentingFeatureFormatter(Translation i18n) {
        this(i18n, Indent.DEFAULT());
    }


    public IndentingFeatureFormatter(Translation i18n, Indent indent) {
        this(i18n, indent, new IndentingFeatureComponentFormatter(i18n, indent));
    }

    public IndentingFeatureFormatter(Translation i18n, Indent indent, FeatureComponentFormatter componentFormatter) {
        this.componentFormatter = componentFormatter;
        this.i18n = i18n;
        this.indent = indent;
    }

    @Override
    public String format(Feature feature) {

        return i18n.translate(FOUND_FEATURE_NAME.key(), feature.getType().getFeatureId().getName(i18n)) + '\n' +
                indent.indentContainedLines(i18n.translate(DESCRIPTION.key(), feature.getType().getFeatureId().getDescription(i18n))) +
                formatComponents(feature);
    }

    private String formatComponents(Feature feature) {
        StringBuilder result = new StringBuilder();
        var components = feature.getComponents();
        if (components.isEmpty()) return result.toString();

        result.append('\n').append('\n');

        for (var featureComponent : feature.getComponents()) {
            result.append(indent.indentContainedLines(componentFormatter.format(featureComponent))).append('\n');
            result.append('\n'); // insert empty line between components
        }
        // remove empty line after last component
        result.deleteCharAt(result.length() - 1);

        // remove newline after last component
        result.deleteCharAt(result.length() - 1);

        return result.toString();
    }

}
