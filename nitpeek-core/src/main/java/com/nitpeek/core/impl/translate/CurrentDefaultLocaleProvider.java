package com.nitpeek.core.impl.translate;

import com.nitpeek.core.api.translate.LocaleProvider;

import java.util.Locale;

public final class CurrentDefaultLocaleProvider implements LocaleProvider {
    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }
}
