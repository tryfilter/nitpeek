package nitpeek.client.application;

import nitpeek.client.application.internal.ApplicationTranslationProvider;
import nitpeek.client.plugin.PluginManager;
import nitpeek.client.plugin.ServiceProviderPluginManager;
import nitpeek.core.api.translate.LocaleProvider;
import nitpeek.core.api.translate.TranslationProvider;

public final class PluginListingApplication implements Application {

    private final LocaleProvider localeProvider;
    private final TranslationProvider translationProvider = new ApplicationTranslationProvider();

    public PluginListingApplication(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }

    @Override
    public void run() {
        var i18n = translationProvider.getTranslation(localeProvider);
        PluginManager manager = new ServiceProviderPluginManager();
        System.out.println(i18n.translate("nitpeek.application.message.LIST_PLUGINS"));
        for (var plugin : manager.getPlugins()) {
            System.out.println(i18n.translate("nitpeek.application.message.DETECTED_PLUGIN" , plugin.getPluginId().getId()));
        }
    }
}
