package nitpeek.client.console;

import nitpeek.client.application.Application;
import nitpeek.client.application.OneShotSuspendApplication;
import nitpeek.client.application.PluginListingApplication;
import nitpeek.core.impl.translate.CurrentDefaultLocaleProvider;

public class Main {
    public static void main(String[] args) throws Exception {

        var localeProvider = new CurrentDefaultLocaleProvider();
        Application app = new OneShotSuspendApplication(() -> new PluginListingApplication(localeProvider).run(), localeProvider);

        app.run();
    }
}
