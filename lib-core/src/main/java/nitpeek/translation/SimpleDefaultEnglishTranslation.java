package nitpeek.translation;

import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.report.FancyTextRangeDescription;

public class SimpleDefaultEnglishTranslation implements Translation {

    private final Translator i18n = new DefaultEnglishTranslator(new FancyTextRangeDescription(this));


    @Override
    public String translate(String translationKey, Object... arguments) {

        var key = InternalTranslationKeys.getForKey(translationKey);

        return switch (key) {
            case MISSING_PAGES_COMPONENT_DESCRIPTION ->
                    i18n.missingPagesComponentDescription((Integer) arguments[0], (Integer) arguments[1]);

            case MISSING_PAGES_FEATURE_NAME -> i18n.missingPagesFeatureName();

            case MISSING_PAGES_FEATURE_DESCRIPTION -> i18n.missingPagesFeatureDescription();

            case PROCESSED_PAGES_FEATURE_NAME -> i18n.processedPagesFeatureName();

            case PROCESSED_PAGES_FEATURE_DESCRIPTION -> i18n.processedPagesFeatureDescription();

            case PROCESSED_PAGES_COMPONENT_DESCRIPTION_CHUNK ->
                    i18n.processedPagesComponentDescriptionChunk((Integer) arguments[0], (Integer) arguments[1]);

            case PROCESSED_PAGES_COMPONENT_DESCRIPTION ->
                    i18n.processedPagesComponentDescription((Integer) arguments[0], (Integer) arguments[1], (Integer) arguments[2]);

            case PROCESSED_SINGLE_PAGE_COMPONENT_DESCRIPTION_CHUNK ->
                    i18n.processedSinglePageComponentDescription((Integer) arguments[0]);

            case FOUND_FEATURE_NAME -> i18n.foundFeatureName((String) arguments[0]);

            case DESCRIPTION -> i18n.description((String) arguments[0]);

            case TEXT_MATCH -> i18n.textMatch((String) arguments[0]);

            case PAGE -> i18n.page();

            case LINE -> i18n.line();

            case CHARACTER -> i18n.character();

            case AXIS_COMPOUND -> i18n.axisCompound((String) arguments[0], (Integer) arguments[1]);

            case AXIS_PINPOINT -> i18n.axisPinpoint((String) arguments[0], (Integer) arguments[1]);

            case AXIS_START -> i18n.axisStart((String) arguments[0], (Integer) arguments[1]);

            case AXIS_END -> i18n.axisEnd((String) arguments[0], (Integer) arguments[1]);

            case DESCRIBE_PAGE_PROCESSING_INFO_RULE_NAME -> i18n.describePageProcessingInfoRuleName();

            case DESCRIBE_PAGE_PROCESSING_INFO_RULE_DESCRIPTION -> i18n.describePageProcessingInfoRuleDescription();

            case APPLIED_RULE_NAME -> i18n.appliedRuleName((String) arguments[0]);

            case APPLIED_RULE_DESCRIPTION -> i18n.appliedRuleDescription((String) arguments[0]);

            case REPLACE_LITERAL_FEATURE_NAME -> i18n.replaceLiteralFeatureName();

            case REPLACE_LITERAL_FEATURE_DESCRIPTION -> i18n.replaceLiteralFeatureDescription();

            case REPLACE_LITERAL_COMPONENT_DESCRIPTION ->
                    i18n.replaceLiteralComponentDescription((String) arguments[0]);

            case DEBUG_FEATURE_NAME -> i18n.debugFeatureName();

            case DEBUG_FEATURE_DESCRIPTION -> i18n.debugFeatureDescription();

            case REPLACE_REGEX_FEATURE_NAME -> i18n.replaceRegexFeatureName();

            case REPLACE_REGEX_FEATURE_DESCRIPTION -> i18n.replaceRegexFeatureDescription();

            case UNPAIRED_PARENTHESES_FEATURE_NAME -> i18n.unpairedParenthesesFeatureName();

            case UNPAIRED_PARENTHESES_FEATURE_DESCRIPTION -> i18n.unpairedParenthesesFeatureDescription();

            case UNPAIRED_OPEN_PARENTHESIS_COMPONENT_DESCRIPTION ->
                    i18n.unpairedOpenParenthesisComponentDescription((String) arguments[0]);

            case UNPAIRED_CLOSING_PARENTHESIS_COMPONENT_DESCRIPTION ->
                    i18n.unpairedClosingParenthesisComponentDescription((String) arguments[0]);
        };
    }


    @Override
    public String translate(TextSelection textSelection) {
        return i18n.foundFeatureComponentCoordinates(textSelection);
    }
}
