package nitpeek.core.impl.translate;

import nitpeek.core.api.translate.LocaleProvider;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.api.translate.TranslationProvider;

public final class WrappingTranslationProvider implements TranslationProvider {

    private final TranslationProvider mainTranslationProvider;
    private final TranslationProvider fallbackTranslationProvider;

    public WrappingTranslationProvider(TranslationProvider mainTranslationProvider, TranslationProvider fallbackTranslationProvider) {
        this.mainTranslationProvider = mainTranslationProvider;
        this.fallbackTranslationProvider = fallbackTranslationProvider;
    }

    @Override
    public Translation getTranslation(LocaleProvider localeProvider) {
        return new WrappingTranslation(
                mainTranslationProvider.getTranslation(localeProvider),
                fallbackTranslationProvider.getTranslation(localeProvider)
        );
    }
}
