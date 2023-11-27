package nitpeek.translation;

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

    private String missingTranslation(String methodName) {
        return messagePrefix + "'" + methodName + "'" + messageBetweenMethodNameAndTranslatorName + translatorName + messageSuffix;
    }
}
