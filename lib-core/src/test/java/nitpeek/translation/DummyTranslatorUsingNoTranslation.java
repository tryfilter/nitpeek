package nitpeek.translation;

final class DummyTranslatorUsingNoTranslation extends DefaultNoTranslationTranslator {
    public DummyTranslatorUsingNoTranslation(String translatorName) {
        super(translatorName);
    }

    public DummyTranslatorUsingNoTranslation(String translatorName, String messagePrefix, String messageBetweenMethodNameAndTranslatorName, String messageSuffix) {
        super(translatorName, messagePrefix, messageBetweenMethodNameAndTranslatorName, messageSuffix);
    }
}
