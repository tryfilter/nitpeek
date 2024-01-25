package nitpeek.core.impl.translate;

import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.impl.report.FancyTextRangeDescription;
import nitpeek.core.impl.translate.helper.DefaultEnglishTranslator;
import nitpeek.core.impl.translate.helper.Translator;

import java.util.function.Function;

public final class CoreEnglishTranslation implements Translation {
    private final Translator i18n = new DefaultEnglishTranslator(new FancyTextRangeDescription(this));


    @Override
    public String translate(String translationKey, Object... arguments) {

        var key = CoreTranslationKeys.getForKey(translationKey);
        if (key == null) return null;

        return switch (key) {
            case MISSING_PAGES_COMPONENT_DESCRIPTION -> translate(2, args ->
                    i18n.missingPagesComponentDescription((Integer) args[0], (Integer) args[1]), arguments);


            case MISSING_PAGES_FEATURE_NAME -> i18n.missingPagesFeatureName();

            case MISSING_PAGES_FEATURE_DESCRIPTION -> i18n.missingPagesFeatureDescription();

            case PROCESSED_PAGES_FEATURE_NAME -> i18n.processedPagesFeatureName();

            case PROCESSED_PAGES_FEATURE_DESCRIPTION -> i18n.processedPagesFeatureDescription();

            case PROCESSED_PAGES_COMPONENT_DESCRIPTION_CHUNK -> translate(2, args ->
                    i18n.processedPagesComponentDescriptionChunk((Integer) args[0], (Integer) args[1]), arguments);

            case PROCESSED_PAGES_COMPONENT_DESCRIPTION -> translate(3, args ->
                    i18n.processedPagesComponentDescription((Integer) args[0], (Integer) args[1], (Integer) args[2]), arguments);

            case PROCESSED_SINGLE_PAGE_COMPONENT_DESCRIPTION_CHUNK -> translate(1, args ->
                    i18n.processedSinglePageComponentDescription((Integer) args[0]), arguments);

            case FOUND_FEATURE_NAME -> translate(1, args ->
                    i18n.foundFeatureName(args[0].toString()), arguments);

            case DESCRIPTION -> translate(1, args ->
                    i18n.description(args[0].toString()), arguments);

            case TEXT_MATCH -> translate(1, args ->
                    i18n.textMatch(args[0].toString()), arguments);

            case PAGE -> i18n.page();

            case LINE -> i18n.line();

            case CHARACTER -> i18n.character();

            case AXIS_COMPOUND -> translate(2, args ->
                    i18n.axisCompound(args[0].toString(), (Integer) args[1]), arguments);

            case AXIS_PINPOINT -> translate(2, args ->
                    i18n.axisPinpoint(args[0].toString(), (Integer) args[1]), arguments);

            case AXIS_START -> translate(2, args ->
                    i18n.axisStart(args[0].toString(), (Integer) args[1]), arguments);

            case AXIS_END -> translate(2, args ->
                    i18n.axisEnd(args[0].toString(), (Integer) args[1]), arguments);

            case DESCRIBE_PAGE_PROCESSING_INFO_RULE_NAME -> i18n.describePageProcessingInfoRuleName();

            case DESCRIBE_PAGE_PROCESSING_INFO_RULE_DESCRIPTION -> i18n.describePageProcessingInfoRuleDescription();

            case APPLIED_RULE_NAME -> translate(1, args ->
                    i18n.appliedRuleName(args[0].toString()), arguments);

            case APPLIED_RULE_DESCRIPTION -> translate(1, args ->
                    i18n.appliedRuleDescription(args[0].toString()), arguments);

            case REPLACE_LITERAL_FEATURE_NAME -> i18n.replaceLiteralFeatureName();

            case REPLACE_LITERAL_FEATURE_DESCRIPTION -> i18n.replaceLiteralFeatureDescription();

            case REPLACE_LITERAL_COMPONENT_DESCRIPTION -> translate(1, args ->
                    i18n.replaceLiteralComponentDescription(args[0].toString()), arguments);

            case DEBUG_FEATURE_NAME -> i18n.debugFeatureName();

            case DEBUG_FEATURE_DESCRIPTION -> i18n.debugFeatureDescription();

            case REPLACE_REGEX_FEATURE_NAME -> i18n.replaceRegexFeatureName();

            case REPLACE_REGEX_FEATURE_DESCRIPTION -> i18n.replaceRegexFeatureDescription();

            case UNPAIRED_PARENTHESES_FEATURE_NAME -> i18n.unpairedParenthesesFeatureName();

            case UNPAIRED_PARENTHESES_FEATURE_DESCRIPTION -> i18n.unpairedParenthesesFeatureDescription();

            case UNPAIRED_OPEN_PARENTHESIS_COMPONENT_DESCRIPTION -> translate(1, args ->
                    i18n.unpairedOpenParenthesisComponentDescription(args[0].toString()), arguments);

            case UNPAIRED_CLOSING_PARENTHESIS_COMPONENT_DESCRIPTION -> translate(1, args ->
                    i18n.unpairedClosingParenthesisComponentDescription(args[0].toString()), arguments);
            case PLUGIN_ALL_RULES_ID_NAME -> translate(1, args ->
                    i18n.pluginAllRulesIdName(args[0].toString()), arguments);
            case PLUGIN_ALL_RULES_ID_DESCRIPTION -> translate(1, args ->
                    i18n.pluginAllRulesIdDescription(args[0].toString()), arguments);
        };
    }


    private String translate(int minArgumentCount, Function<Object[], String> mapper, Object[] arguments) {
        if (minArgumentCount > arguments.length) return null;

        return mapper.apply(arguments);
    }


    @Override
    public String translate(TextSelection textSelection) {
        return i18n.foundFeatureComponentCoordinates(textSelection);
    }
}
