package nitpeek.client.plugin.test;

import nitpeek.client.plugin.PluginManager;
import nitpeek.client.plugin.ServiceProviderPluginManager;
import nitpeek.core.api.plugin.Plugin;
import nitpeek.core.api.translate.LocaleProvider;
import nitpeek.core.impl.process.SimpleProcessor;
import nitpeek.core.impl.process.StringPageSource;
import nitpeek.core.impl.translate.CurrentDefaultLocaleProvider;
import nitpeek.core.impl.translate.FallbackCoreTranslationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * NOTE: Due to current limitations regarding IntelliJ and the module path, these tests only pass when run
 * with Gradle. <br>
 * <br>
 * This test class resides in a different module from the class under test to illustrate the interaction between
 * modules. <br>
 * Note that the plugin (in this example demo-plugin) needs to reside on the module path in order to be picked up by
 * {@link ServiceProviderPluginManager}. <br>
 * Also note that this test is strongly tied to the particular implementation of demo-plugin. It serves as an example of
 * how to use a {@link ServiceProviderPluginManager} and the {@link nitpeek.core.api.plugin.Plugin}s it provides.
 */
final class ServiceProviderPluginManagerWithDemoPluginShould {

    private final PluginManager pluginManager = new ServiceProviderPluginManager();
    private final LocaleProvider localeProvider = new CurrentDefaultLocaleProvider();
    private final Plugin demoPlugin = pluginManager.getPlugins().stream().findFirst().orElseThrow();

    @BeforeEach
    void setup() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    void getPluginsProvidedByDemoPlugin() {
        var plugins = pluginManager.getPlugins();
        assertEquals(1, plugins.size());
    }

    @Test
    void enableGettingDefaultTranslatedStrings() {

        var translation = demoPlugin.getTranslationProvider().getTranslation(localeProvider);

        String expected = "Demo Plugin #1";
        String actual = demoPlugin.getPluginId().getName(translation);

        assertEquals(expected, actual);
    }

    @Test
    void enableGettingDefaultTranslatedFeatures() {

        var translationProvider = new FallbackCoreTranslationProvider(demoPlugin.getTranslationProvider());

        var translation = translationProvider.getTranslation(localeProvider);

        var pages = new StringPageSource("42 41 42 40 Seven fourty niner 49");
        // the 'MEANING_OF_LIFE' rule-set should produce a feature for each instance of '42' as well as each
        // instance of '4X' with X in 0, 1, 3..9 (coming from 2 distinct rules), meaning 5 total features for the string given by pages

        var processor = processorForRuleSetWithId("nitpeek.demo.plugin1.MEANING_OF_LIFE");
        processor.startProcessing(pages);
        var someComponent = processor.getFeatures().getFirst().getComponents().getFirst();
        // the rules of the 'MEANING_OF_LIFE' rule-set use LiteralReplacer/RegexReplacer analyzers. These produce feature
        // components which, using the default english translation, contain the phrase 'Replace' in their description
        assertTrue(someComponent.getDescription(translation).contains("Replace"));
    }

    @Test
    void enableGettingOtherLanguageTranslatedStrings() {

        Locale.setDefault(Locale.GERMAN);
        var translation = demoPlugin.getTranslationProvider().getTranslation(localeProvider);

        String expected = "Dies ist Beispielsplugin Nummer eins. Es ist ausschließlich zur Darstellung der Plugin API nützlich.";
        String actual = demoPlugin.getPluginId().getDescription(translation);

        assertEquals(expected, actual);
    }

    @Test
    void getCustomRules() {

        var rules = demoPlugin.getAllRules().getRules();
        // somewhat arbitrary: demo-plugin provides 3 distinct rules
        assertEquals(3, rules.size());
    }

    @Test
    void getRuleSets() {

        var ruleSets = demoPlugin.getRuleSetProviders();
        // somewhat arbitrary: demo-plugin provides 2 custom rule sets
        assertEquals(2, ruleSets.size());
    }

    @Test
    void useRuleSetMeaningOfLife() {

        var pages = new StringPageSource("42 41 42 40 Seven fourty niner 49");
        // the 'MEANING_OF_LIFE' rule-set should produce a feature for each instance of '42' as well as each
        // instance of '4X' with X in 0, 1, 3..9 (coming from 2 distinct rules), meaning 5 total features for the string given by pages

        var processor = processorForRuleSetWithId("nitpeek.demo.plugin1.MEANING_OF_LIFE");
        processor.startProcessing(pages);
        var features = processor.getFeatures();

        assertEquals(5, features.size());
    }

    @Test
    void useRuleSetDelete() {

        var pages = new StringPageSource("testiNG, testing, so much TESTING!");
        // the 'DELETION' rule-set should produce a feature for each (case-insensitive) instance of 'test' (coming
        // from one rule), meaning 3 total features for the string given by pages

        var processor = processorForRuleSetWithId("nitpeek.demo.plugin1.DELETION");
        processor.startProcessing(pages);
        var features = processor.getFeatures();

        assertEquals(3, features.size());
    }

    private SimpleProcessor processorForRuleSetWithId(String id) {
        var ruleSets = demoPlugin.getRuleSetProviders().stream()
                .filter(ruleSetProvider -> ruleSetProvider.getRuleSetId().getId().equals(id)).toList();

        assertEquals(1, ruleSets.size());

        return new SimpleProcessor(ruleSets.getFirst());
    }
}