package nitpeek.core.api.report;

import nitpeek.core.api.common.Feature;
import nitpeek.translation.DefaultEnglishTranslator;
import nitpeek.translation.Translator;

public final class IndentingFeatureFormatter implements FeatureFormatter {


    private final FeatureComponentFormatter componentFormatter;
    private final Indent indent;
    private final Translator i18n;


    public IndentingFeatureFormatter() {
        this(new DefaultEnglishTranslator());
    }


    public IndentingFeatureFormatter(Translator i18n) {
        this(i18n, Indent.DEFAULT());
    }


    public IndentingFeatureFormatter(Translator i18n, Indent indent) {
        this(i18n, indent, new IndentingFeatureComponentFormatter(i18n, indent));
    }

    public IndentingFeatureFormatter(Translator i18n, Indent indent, FeatureComponentFormatter componentFormatter) {
        this.componentFormatter = componentFormatter;
        this.i18n = i18n;
        this.indent = indent;
    }

    @Override
    public String format(Feature feature) {

        return i18n.foundFeatureName(feature.getType().name()) + '\n' +
                indent.indentContainedLines(i18n.description(feature.getType().description())) + '\n' +
                '\n' +
                formatComponents(feature);
    }

    private String formatComponents(Feature feature) {
        StringBuilder result = new StringBuilder();
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
