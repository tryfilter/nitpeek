package com.nitpeek.client.application;

import com.nitpeek.client.application.internal.ApplicationTranslationProvider;
import com.nitpeek.client.plugin.PluginManager;
import com.nitpeek.client.plugin.ServiceProviderPluginManager;
import com.nitpeek.core.api.translate.LocaleProvider;
import com.nitpeek.core.api.translate.TranslationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PluginListingApplication implements Application {

    private final Logger log = LoggerFactory.getLogger(PluginListingApplication.class);

    private final LocaleProvider localeProvider;
    private final TranslationProvider translationProvider = new ApplicationTranslationProvider();

    public PluginListingApplication(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }

    @Override
    public void run() {
        var i18n = translationProvider.getTranslation(localeProvider);
        PluginManager manager = new ServiceProviderPluginManager();
        log.atDebug().log(i18n.translate("com.nitpeek.application.message.LIST_PLUGINS"));
        var plugins = manager.getPlugins();
        if (plugins.isEmpty()) log.atWarn().log(i18n.translate("com.nitpeek.application.message.DETECTED_NO_PLUGINS"));
        for (var plugin : plugins) {
            log.atDebug().log(i18n.translate("com.nitpeek.application.message.DETECTED_PLUGIN" , plugin.getPluginId().getId()));
        }
    }
}
