package nitpeek.plugins.demoplugin.plugin1.internal;

import nitpeek.core.impl.translate.ResourceBundleTranslationProvider;


public final class DemoTranslationProvider extends ResourceBundleTranslationProvider {

    @Override
    protected String getBaseResourcePath() {
        return "nitpeek.plugins.demoplugin.plugin1.DemoBundle";
    }
}
