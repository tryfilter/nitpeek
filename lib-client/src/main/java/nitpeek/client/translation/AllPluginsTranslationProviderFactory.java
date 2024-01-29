package nitpeek.client.translation;

import nitpeek.client.plugin.PluginManager;
import nitpeek.core.api.plugin.Plugin;
import nitpeek.core.api.translate.TranslationProvider;
import nitpeek.core.impl.translate.FallbackCoreTranslationProvider;
import nitpeek.core.impl.translate.NoOpTranslation;
import nitpeek.core.impl.translate.WrappingTranslationProvider;

// The infamous "Factory factory". 
public final class AllPluginsTranslationProviderFactory implements TranslationProviderFactory {

    private final PluginManager pluginManager;

    public AllPluginsTranslationProviderFactory(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public TranslationProvider createTranslationProvider() {

        var plugins = pluginManager.getPlugins();
        if (plugins.isEmpty()) return new FallbackCoreTranslationProvider();

        TranslationProvider noOp = localeProvider -> new NoOpTranslation();
        var combinedTranslationProvider = plugins.stream()
                .map(Plugin::getTranslationProvider)
                .reduce(noOp, WrappingTranslationProvider::new);

        return new FallbackCoreTranslationProvider(combinedTranslationProvider);
    }
}
