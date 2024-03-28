package nitpeek.client.application;

import nitpeek.client.application.internal.ApplicationTranslationProvider;
import nitpeek.core.api.translate.LocaleProvider;
import nitpeek.core.api.translate.TranslationProvider;

import java.io.IOException;

public final class SuspendOnExceptionApplication implements Application {

    private final Application application;
    private final LocaleProvider localeProvider;
    private final TranslationProvider translationProvider = new ApplicationTranslationProvider();

    public SuspendOnExceptionApplication(Application application, LocaleProvider localeProvider) {
        this.application = application;
        this.localeProvider = localeProvider;
    }

    @Override
    public void run() throws IOException {
        try {
            application.run();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            var i18n = translationProvider.getTranslation(localeProvider);
            System.out.println();
            System.out.println(i18n.translate("nitpeek.application.message.PRESS_KEY"));
            System.in.read();
        }
    }
}