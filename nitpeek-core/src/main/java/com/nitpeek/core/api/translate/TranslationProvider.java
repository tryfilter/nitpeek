package com.nitpeek.core.api.translate;

public interface TranslationProvider {

    Translation getTranslation(LocaleProvider localeProvider);
}
