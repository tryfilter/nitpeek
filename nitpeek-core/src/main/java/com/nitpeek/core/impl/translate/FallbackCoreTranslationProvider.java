package com.nitpeek.core.impl.translate;

import com.nitpeek.core.api.translate.LocaleProvider;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.core.api.translate.TranslationProvider;

public final class FallbackCoreTranslationProvider implements TranslationProvider {
    private final TranslationProvider translationProvider;

    public FallbackCoreTranslationProvider() {
        this(null);
    }

    public FallbackCoreTranslationProvider(TranslationProvider translationProvider) {
        this.translationProvider = translationProvider;
    }

    @Override
    public Translation getTranslation(LocaleProvider localeProvider) {
        var fallbackTranslation = getFallbackTranslation(localeProvider);
        if (translationProvider != null)
            return new WrappingTranslation(translationProvider.getTranslation(localeProvider), fallbackTranslation);

        return fallbackTranslation;
    }

    private Translation getFallbackTranslation(LocaleProvider localeProvider) {
        return new DefaultFallbackEnglishTranslation();
    }
}
