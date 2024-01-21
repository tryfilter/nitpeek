package nitpeek.translation;

public interface TranslationProvider {

    Translation getTranslation(LocaleProvider localeProvider);
}
