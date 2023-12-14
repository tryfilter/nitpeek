package nitpeek.translation;

import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.report.FancyTextRangeDescription;
import nitpeek.core.api.report.TextRangeDescription;

/**
 * This class may be extended to create partially implemented translators or to create custom translators that are
 * resilient against new additions to the Translator interface: instead of a compilation/runtime error, yet not-implemented
 * methods will defer to this default english translation.
 */
public class DefaultEnglishTranslator implements Translator {

    private final TextRangeDescription description;

    public DefaultEnglishTranslator() {
        description = new FancyTextRangeDescription(this);
    }

    DefaultEnglishTranslator(TextRangeDescription description) {
        this.description = description;
    }

    @Override
    public String missingPagesComponentDescription(int firstMissingPage, int lastMissingPage) {
        return "Pages " + firstMissingPage + "-" + lastMissingPage + " have not been processed.";
    }

    @Override
    public String missingPagesFeatureName() {
        return "Missing pages";
    }

    @Override
    public String missingPagesFeatureDescription() {
        return "Fewer pages were processed than expected.";
    }

    @Override
    public String processedPagesFeatureName() {
        return "Processed pages";
    }

    @Override
    public String processedPagesFeatureDescription() {
        return "Counts the number of pages that were processed and lists them.";
    }

    @Override
    public String processedPagesComponentDescription(int firstProcessedPage, int lastProcessedPage) {
        if (firstProcessedPage == lastProcessedPage) return "page " + firstProcessedPage;
        else return "pages " + firstProcessedPage + "-" + lastProcessedPage;
    }

    @Override
    public String processedPagesComponentDescription(int firstProcessedPage, int lastProcessedPage, int pageCount) {
        return "Processed " + pageCount + " total pages from page " + firstProcessedPage + " to page " + lastProcessedPage;
    }

    @Override
    public String foundFeatureName(String featureName) {
        return "Found feature '" + featureName + "'";
    }

    @Override
    public String description(String featureName) {
        return "Description: " + featureName;
    }

    @Override
    public String textMatch(String textMatch) {
        return "Text Match: " + textMatch;
    }

    @Override
    public String foundFeatureComponentCoordinates(TextSelection coordinates) {
        return description.describe(coordinates);
    }

    @Override
    public String page() {
        return "page";
    }

    @Override
    public String line() {
        return "line";
    }

    @Override
    public String character() {
        return "character";
    }

    @Override
    public String axisCompound(String axisName, int value) {
        return ", " + axisName + " " + value;
    }

    @Override
    public String axisPinpoint(String axisName, int value) {
        return "at " + axisName + " " + value;
    }

    @Override
    public String axisStart(String axisName, int value) {
        return "from " + axisName + " " + value;
    }

    @Override
    public String axisEnd(String axisName, int value) {
        return "to " + axisName + " " + value;
    }

    @Override
    public String describePageProcessingInfoRuleName() {
        return "Process Page Information";
    }

    @Override
    public String describePageProcessingInfoRuleDescription() {
        return "This rule scans the text and reports various pieces of information about it," +
                " like the number of pages that were processed, any pages that were skipped, etc.";
    }

    @Override
    public String appliedRuleName(String ruleName) {
        return "Applied rule \"" + ruleName + "\"";
    }

    @Override
    public String appliedRuleDescription(String ruleDescription) {
        return "(" + ruleDescription + ")";
    }

    @Override
    public String replaceLiteralFeatureName() {
        return "Replace value";
    }

    @Override
    public String replaceLiteralFeatureDescription() {
        return "Replaces given values, each with a corresponding replacement value.";
    }

    @Override
    public String replaceLiteralComponentDescription(String newValue) {
        return "Replace with '" + newValue + "'";
    }

    @Override
    public String debugFeatureName() {
        return "Debug Feature";
    }

    @Override
    public String debugFeatureDescription() {
        return "This is a place-holder Feature intended for testing purposes.";
    }
}
