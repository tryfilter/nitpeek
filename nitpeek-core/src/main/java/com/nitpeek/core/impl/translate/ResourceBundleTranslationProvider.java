package com.nitpeek.core.impl.translate;

import com.nitpeek.core.api.translate.LocaleProvider;
import com.nitpeek.core.api.translate.Translation;
import com.nitpeek.core.api.translate.TranslationProvider;

import java.util.ResourceBundle;

/**
 * Note that in order for the Translation objects created by this provider to be able to access the resource bundles
 * that your implementation class uses, the module that your implementation class resides in must open the package
 * containing your implementation class to the module com.nitpeek.lib.core
 */
public abstract class ResourceBundleTranslationProvider implements TranslationProvider {


    protected abstract String getBaseResourcePath();

    @Override
    public Translation getTranslation(LocaleProvider localeProvider) {
        var locale = localeProvider.getLocale();
        var resourceBundle = ResourceBundle.getBundle(getBaseResourcePath(), locale, this.getClass().getModule());

        return new ResourceBundleTranslation(resourceBundle, locale);
    }
}
