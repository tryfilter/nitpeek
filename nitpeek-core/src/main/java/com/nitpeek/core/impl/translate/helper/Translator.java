package com.nitpeek.core.impl.translate.helper;

import com.nitpeek.core.api.common.TextSelection;

/**
 * This interface is likely to be expanded in the future and breaking compatibility with existing implementors is an
 * expected consequence.<br>
 * If you wish to avoid breakage of your implementation when this interface is expanded then subclass one of the default
 * translators in this package, e.g. DefaultEnglishTranslator. That way, any methods added to this interface that your
 * implementation is not yet supporting can produce sensible default translations.<br>
 * If extending a default translator is not an option for you, you may use the delegation pattern instead.
 */
public interface Translator {

    String missingPagesComponentDescription(int firstMissingPage, int lastMissingPage);

    String missingPagesFeatureName();

    String missingPagesFeatureDescription();

    String processedPagesFeatureName();

    String processedPagesFeatureDescription();

    String processedPagesComponentDescriptionChunk(int firstProcessedPage, int lastProcessedPage);

    String processedSinglePageComponentDescription(int processedPage);

    String processedPagesComponentDescription(int firstProcessedPage, int lastProcessedPage, int pageCount);

    String foundFeatureName(String featureName);

    String description(String featureName);

    String textMatch(String textMatch);

    String foundFeatureComponentCoordinates(TextSelection coordinates);

    String page();

    String line();

    String character();

    String axisCompound(String axisName, int value);

    String axisPinpoint(String axisName, int value);

    String axisStart(String axisName, int value);

    String axisEnd(String axisName, int value);

    String describePageProcessingInfoRuleName();

    String describePageProcessingInfoRuleDescription();

    String appliedRuleName(String ruleName);

    String appliedRuleDescription(String ruleDescription);

    String replaceLiteralFeatureName();

    String replaceLiteralFeatureDescription();

    String replaceLiteralComponentDescription(String newValue);

    String debugFeatureName();

    String debugFeatureDescription();

    String replaceRegexFeatureName();

    String replaceRegexFeatureDescription();

    String unpairedParenthesesFeatureName();

    String unpairedParenthesesFeatureDescription();

    String unpairedOpenParenthesisComponentDescription(String missingParenthesis);

    String unpairedClosingParenthesisComponentDescription(String missingParenthesis);

    String pluginAllRulesIdName(String pluginId);

    String pluginAllRulesIdDescription(String pluginId);

    String replaceLiteralComponentDescriptionDelete();

    String ruleSetAggregateIdName();

    String ruleSetAggregateIdDescription(String ruleSetList);
}
