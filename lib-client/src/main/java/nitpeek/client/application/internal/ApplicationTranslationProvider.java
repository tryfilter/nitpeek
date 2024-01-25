package nitpeek.client.application.internal;

import nitpeek.core.impl.translate.ResourceBundleTranslationProvider;

public class ApplicationTranslationProvider extends ResourceBundleTranslationProvider {

    @Override
    protected String getBaseResourcePath() {
        return "nitpeek.client.application.internal.ApplicationTranslation";
    }
}
