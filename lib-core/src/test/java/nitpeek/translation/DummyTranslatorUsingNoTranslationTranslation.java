package nitpeek.translation;

import nitpeek.core.impl.translate.helper.DefaultNoTranslationTranslation;

final class DummyTranslatorUsingNoTranslationTranslation extends DefaultNoTranslationTranslation {
    public DummyTranslatorUsingNoTranslationTranslation(String translatorName) {
        super(translatorName);
    }

    public DummyTranslatorUsingNoTranslationTranslation(String translatorName, String messagePrefix, String messageBetweenMethodNameAndTranslatorName, String messageSuffix) {
        super(translatorName, messagePrefix, messageBetweenMethodNameAndTranslatorName, messageSuffix);
    }
}