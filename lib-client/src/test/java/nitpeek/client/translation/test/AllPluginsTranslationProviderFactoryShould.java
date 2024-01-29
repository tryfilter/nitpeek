package nitpeek.client.translation.test;

import nitpeek.client.plugin.PluginManager;
import nitpeek.client.translation.AllPluginsTranslationProviderFactory;
import nitpeek.client.translation.TranslationProviderFactory;
import nitpeek.core.api.plugin.Plugin;
import nitpeek.core.api.translate.LocaleProvider;
import nitpeek.core.api.translate.Translation;
import nitpeek.core.api.translate.TranslationProvider;
import nitpeek.core.impl.translate.CoreTranslationKeys;
import nitpeek.core.impl.translate.CurrentDefaultLocaleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class AllPluginsTranslationProviderFactoryShould {

    @Mock private PluginManager pluginManager;

    @Mock private Plugin plugin1, plugin2, plugin3;

    @Mock private TranslationProvider dummyTranslationProvider;

    @Mock private Translation noopTranslation;

    private final LocaleProvider localeProvider = new CurrentDefaultLocaleProvider();

    private TranslationProviderFactory factory;

    @BeforeEach
    void setup() {
        Locale.setDefault(Locale.ENGLISH);

        when(plugin1.getTranslationProvider()).thenReturn(dummyTranslationProvider);
        when(plugin2.getTranslationProvider()).thenReturn(dummyTranslationProvider);
        when(plugin3.getTranslationProvider()).thenReturn(dummyTranslationProvider);
        when(dummyTranslationProvider.getTranslation(any(LocaleProvider.class))).thenReturn(noopTranslation);

        when(pluginManager.getPlugins()).thenReturn(Set.of(plugin1, plugin2, plugin3));
        factory = new AllPluginsTranslationProviderFactory(pluginManager);
    }

    @Test
    void fallbackToCoreTranslationWhenNoPluginRecognizesKey() {
        var missingPagesKey = CoreTranslationKeys.MISSING_PAGES_FEATURE_NAME.key();
        var combinedPluginTranslation = factory.createTranslationProvider().getTranslation(localeProvider);

        var expected = "Missing pages"; // English translation of fallback translation-provider
        var actual = combinedPluginTranslation.translate(missingPagesKey);
        assertEquals(expected, actual);
    }

    @Test
    void preferTranslationFromPluginWhenAvailable() {
        var customTranslationProvider = mock(TranslationProvider.class);
        var customTranslation = mock(Translation.class);
        when(plugin2.getTranslationProvider()).thenReturn(customTranslationProvider);
        when(customTranslationProvider.getTranslation(any(LocaleProvider.class))).thenReturn(customTranslation);

        var expected = "Plugin 1 Custom 'Missing pages'";
        var missingPagesKey = CoreTranslationKeys.MISSING_PAGES_FEATURE_NAME.key();
        when(customTranslation.translate(missingPagesKey)).thenReturn(expected);
        var combinedPluginTranslation = factory.createTranslationProvider().getTranslation(localeProvider);

        var actual = combinedPluginTranslation.translate(missingPagesKey);
        assertEquals(expected, actual);
    }
}