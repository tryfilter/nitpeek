package com.nitpeek.core.impl.report;

import com.nitpeek.core.api.common.FeatureComponent;
import com.nitpeek.core.api.report.FeatureComponentFormatter;
import com.nitpeek.core.api.translate.Translation;

import java.util.Optional;

import static com.nitpeek.core.impl.translate.CoreTranslationKeys.DESCRIPTION;
import static com.nitpeek.core.impl.translate.CoreTranslationKeys.TEXT_MATCH;

public final class IndentingFeatureComponentFormatter implements FeatureComponentFormatter {

    private final Indent indent;
    private final Translation i18n;


    public IndentingFeatureComponentFormatter(Translation i18n) {
        this(i18n, Indent.DEFAULT());
    }

    public IndentingFeatureComponentFormatter(Translation i18n, Indent indent) {
        this.indent = indent;
        this.i18n = i18n;
    }

    @Override
    public String format(FeatureComponent featureComponent) {

        var result = new StringBuilder();
        result.append(i18n.translate(featureComponent.getCoordinates())).append('\n');
        result.append(indent.indentContainedLines(i18n.translate(DESCRIPTION.key(), featureComponent.getDescription(i18n)))).append('\n');
        Optional<String> textMatch = featureComponent.getRelevantTextPortion();
        textMatch.ifPresent(match -> result.append(indent.indentContainedLines(i18n.translate(TEXT_MATCH.key(), match))));

        return result.toString();
    }
}
