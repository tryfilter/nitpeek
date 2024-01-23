package nitpeek.core.impl.translate;

import nitpeek.core.api.translate.LocaleProvider;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.api.translate.TranslationProvider;

import java.util.ResourceBundle;

public abstract class ResourceBundleTranslationProvider implements TranslationProvider {


    protected abstract String getBaseResourcePath();

    @Override
    public Translation getTranslation(LocaleProvider localeProvider) {
        var locale = localeProvider.getLocale();
        var resourceBundle = ResourceBundle.getBundle(getBaseResourcePath(), locale, this.getClass().getModule());

        return new ResourceBundleTranslation(resourceBundle, locale);
    }
}
