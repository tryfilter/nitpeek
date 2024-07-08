package com.nitpeek.plugins.demoplugin.plugin1.internal;

import com.nitpeek.core.impl.translate.ResourceBundleTranslationProvider;


public final class DemoTranslationProvider extends ResourceBundleTranslationProvider {

    @Override
    protected String getBaseResourcePath() {
        return "com.nitpeek.plugins.demoplugin.plugin1.DemoBundle";
    }
}
