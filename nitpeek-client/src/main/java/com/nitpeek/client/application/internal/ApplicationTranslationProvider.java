package com.nitpeek.client.application.internal;

import com.nitpeek.core.impl.translate.ResourceBundleTranslationProvider;

public final class ApplicationTranslationProvider extends ResourceBundleTranslationProvider {

    @Override
    protected String getBaseResourcePath() {
        return "com.nitpeek.client.application.internal.ApplicationTranslation";
    }
}
