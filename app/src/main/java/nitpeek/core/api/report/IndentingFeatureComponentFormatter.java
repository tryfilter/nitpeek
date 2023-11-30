package nitpeek.core.api.report;

import nitpeek.core.api.common.FeatureComponent;
import nitpeek.translation.DefaultEnglishTranslator;
import nitpeek.translation.Translator;

import java.util.Optional;

public final class IndentingFeatureComponentFormatter implements FeatureComponentFormatter {

    private final Indent indent;
    private final Translator i18n;

    public IndentingFeatureComponentFormatter() {
        this(new DefaultEnglishTranslator());
    }

    public IndentingFeatureComponentFormatter(Translator translator) {
        this(translator, Indent.DEFAULT());
    }

    public IndentingFeatureComponentFormatter(Translator translator, Indent indent) {
        this.indent = indent;
        this.i18n = translator;
    }

    @Override
    public String format(FeatureComponent featureComponent) {

        var result = new StringBuilder();
        result.append(i18n.foundFeatureComponentCoordinates(featureComponent.getCoordinates())).append('\n');
        result.append(indent.indentContainedLines(i18n.description(featureComponent.getDescription()))).append('\n');
        Optional<String> textMatch = featureComponent.getRelevantTextPortion();
        textMatch.ifPresent(match -> result.append(indent.indentContainedLines(i18n.textMatch(match))));

        return result.toString();
    }
}
