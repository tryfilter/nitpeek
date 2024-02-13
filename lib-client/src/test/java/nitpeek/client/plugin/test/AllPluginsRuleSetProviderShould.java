package nitpeek.client.plugin.test;

import nitpeek.client.plugin.AllPluginsRuleSetProvider;
import nitpeek.client.plugin.PluginManager;
import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.plugin.Plugin;
import nitpeek.core.api.process.RuleSetProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
final class AllPluginsRuleSetProviderShould {

    @Mock private PluginManager pluginManager;

    @Mock private Plugin plugin12, plugin34, plugin35;

    @Mock private Rule r1, r2, r3, r4, r5;

    @Mock private RuleSetProvider plugin12RuleSet, plugin34RuleSet, plugin35RuleSet;


    @BeforeEach
    void setup() {
        when(plugin12.getRuleSetProviders()).thenReturn(Set.of(plugin12RuleSet));
        when(plugin12RuleSet.getRules()).thenReturn(Set.of(r1, r2));

        when(plugin34.getRuleSetProviders()).thenReturn(Set.of(plugin34RuleSet));
        when(plugin34RuleSet.getRules()).thenReturn(Set.of(r3, r4));
    }


    @Test
    void combineRulesFromPlugins() {
        when(pluginManager.getPlugins()).thenReturn(Set.of(plugin12, plugin34));

        var allPluginRules = new AllPluginsRuleSetProvider(pluginManager);
        var expected = Set.of(r1, r2, r3, r4);
        var actual = allPluginRules.getRules();
        assertEquals(expected, actual);
    }

    @Test
    void updateRulesWhenPluginListChanges() {
        when(plugin35.getRuleSetProviders()).thenReturn(Set.of(plugin35RuleSet));
        when(plugin35RuleSet.getRules()).thenReturn(Set.of(r3, r5));
        when(pluginManager.getPlugins())
                .thenReturn(Set.of(plugin12, plugin34))
                .thenReturn(Set.of(plugin12, plugin35));

        var allPluginRules = new AllPluginsRuleSetProvider(pluginManager);

        var expectedBeforePluginChange = Set.of(r1, r2, r3, r4);
        assertEquals(expectedBeforePluginChange, allPluginRules.getRules());

        var expectedAfterPluginChange = Set.of(r1, r2, r3, r5);
        assertEquals(expectedAfterPluginChange, allPluginRules.getRules());
    }
}