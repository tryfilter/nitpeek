package nitpeek.core.impl.translate.helper;

import nitpeek.core.api.common.TextSelection;
import nitpeek.core.api.translate.Translation;

/**
 * This class may be extended to create partially implemented translators or to create custom translators that are
 * resilient against new additions to the Translator interface: instead of a compilation/runtime error, yet not-implemented
 * methods will defer to this default placeholder message.
 */
public class DefaultNoTranslationTranslation implements Translation {

    private final String translatorName;
    private final String messagePrefix;
    private final String messageSuffix;
    private final String messageBetweenMethodNameAndTranslatorName;

    public DefaultNoTranslationTranslation(String translatorName) {
        this(translatorName, "[missing translation of ", " for translator ", "]");
    }

    public DefaultNoTranslationTranslation(String translatorName, String messagePrefix, String messageBetweenMethodNameAndTranslatorName, String messageSuffix) {
        this.translatorName = translatorName;
        this.messagePrefix = messagePrefix;
        this.messageSuffix = messageSuffix;
        this.messageBetweenMethodNameAndTranslatorName = messageBetweenMethodNameAndTranslatorName;
    }

    private String missingTranslation(String methodName) {
        return messagePrefix + "'" + methodName + "'" + messageBetweenMethodNameAndTranslatorName + translatorName + messageSuffix;
    }

    @Override
    public String translate(String translationKey, Object... arguments) {
        return missingTranslation(translationKey);
    }

    @Override
    public String translate(TextSelection textSelection) {
        return null;
    }
}
