package nitpeek.client.application;

import nitpeek.client.application.internal.ApplicationTranslationProvider;
import nitpeek.core.api.translate.LocaleProvider;
import nitpeek.core.api.translate.TranslationProvider;

public final class OneShotSuspendApplication implements Application {

    private final Runnable oneShotAction;
    private final LocaleProvider localeProvider;
    private final TranslationProvider translationProvider = new ApplicationTranslationProvider();

    public OneShotSuspendApplication(Runnable oneShotAction, LocaleProvider localeProvider) {
        this.oneShotAction = oneShotAction;
        this.localeProvider = localeProvider;
    }

    @Override
    public void run() throws Exception {
        oneShotAction.run();

        var i18n = translationProvider.getTranslation(localeProvider);
        System.out.println(i18n.translate("nitpeek.application.message.PRESS_KEY"));
        System.in.read();
    }
}
