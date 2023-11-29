package nitpeek.translation;

import nitpeek.core.api.common.TextSelection;

/**
 * This class may be extended to create partially implemented translators or to create custom translators that are
 * resilient against new additions to the Translator interface: instead of a compilation/runtime error, yet not-implemented
 * methods will defer to this default placeholder message.
 */
public class DefaultNoTranslationTranslator implements Translator {

    private final String translatorName;
    private final String messagePrefix;
    private final String messageSuffix;
    private final String messageBetweenMethodNameAndTranslatorName;

    public DefaultNoTranslationTranslator(String translatorName) {
        this(translatorName, "[missing translation of ", " for translator ", "]");
    }

    public DefaultNoTranslationTranslator(String translatorName, String messagePrefix, String messageBetweenMethodNameAndTranslatorName, String messageSuffix) {
        this.translatorName = translatorName;
        this.messagePrefix = messagePrefix;
        this.messageSuffix = messageSuffix;
        this.messageBetweenMethodNameAndTranslatorName = messageBetweenMethodNameAndTranslatorName;
    }

    @Override
    public String missingPagesComponentDescription(int firstMissingPage, int lastMissingPage) {
        return missingTranslation("missingPagesComponentDescription");
    }

    @Override
    public String missingPagesFeatureName() {
        return missingTranslation("missingPagesFeatureName");
    }

    @Override
    public String missingPagesFeatureDescription() {
        return missingTranslation("missingPagesFeatureDescription");
    }

    @Override
    public String processedPagesFeatureName() {
        return missingTranslation("processedPagesFeatureName");
    }

    @Override
    public String processedPagesFeatureDescription() {
        return missingTranslation("processedPagesFeatureDescription");
    }

    @Override
    public String processedPagesComponentDescription(int firstProcessedPage, int lastProcessedPage) {
        return missingTranslation("processedPagesComponentDescription");
    }

    @Override
    public String processedPagesComponentDescription(int firstProcessedPage, int lastProcessedPage, int pageCount) {
        return missingTranslation("processedPagesComponentDescription");
    }

    @Override
    public String foundFeatureName(String featureName) {
        return missingTranslation("foundFeatureMessage");
    }

    @Override
    public String description(String featureName) {
        return missingTranslation("description");
    }

    @Override
    public String textMatch(String textMatch) {
        return missingTranslation("textMatch");
    }

    @Override
    public String foundFeatureComponentCoordinates(TextSelection coordinates) {
        return missingTranslation("foundFeatureComponentCoordinates");
    }

    @Override
    public String page() {
        return missingTranslation("page");
    }

    @Override
    public String line() {
        return missingTranslation("line");
    }

    @Override
    public String character() {
        return missingTranslation("character");
    }

    @Override
    public String axisCompound(String axisName, int value) {
        return missingTranslation("axisCompound");
    }

    @Override
    public String axisPinpoint(String axisName, int value) {
        return missingTranslation("axisPinpoint");
    }

    @Override
    public String axisStart(String axisName, int value) {
        return missingTranslation("axisStart");
    }

    @Override
    public String axisEnd(String axisName, int value) {
        return missingTranslation("axisEnd");
    }

    private String missingTranslation(String methodName) {
        return messagePrefix + "'" + methodName + "'" + messageBetweenMethodNameAndTranslatorName + translatorName + messageSuffix;
    }
}
