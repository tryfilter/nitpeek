package com.nitpeek.test.core.impl.process;

import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.impl.process.AggregateRuleSetProvider;
import com.nitpeek.core.impl.translate.CoreEnglishTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class AggregateRuleSetProviderShould {

    @Mock private Rule rule1, rule2, rule3, rule4, rule5;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) private RuleSetProvider provider13, provider25, provider4;

    private AggregateRuleSetProvider aggregateRuleSetProvider;

    @BeforeEach
    void setup() {

        aggregateRuleSetProvider = new AggregateRuleSetProvider(Set.of(provider13, provider25, provider4));
    }

    @Test
    void returnAllComponentRules() {
        when(provider13.getRules()).thenReturn(Set.of(rule1, rule3));
        when(provider25.getRules()).thenReturn(Set.of(rule2, rule5));
        when(provider4.getRules()).thenReturn(Set.of(rule4));

        var allRules = Set.of(rule1, rule2, rule3, rule4, rule5);
        assertEquals(allRules, aggregateRuleSetProvider.getRules());
    }

    @Test
    void preserveIdsOfAggregatedRuleSetProvidersInDescription() {
        String provider13Id = "P13";
        String provider25Id = "P25";
        String provider4Id = "P4";
        when(provider13.getRuleSetId().getId()).thenReturn(provider13Id);
        when(provider25.getRuleSetId().getId()).thenReturn(provider25Id);
        when(provider4.getRuleSetId().getId()).thenReturn(provider4Id);
        var i18n = new CoreEnglishTranslation();
        String aggregateDescription = aggregateRuleSetProvider.getRuleSetId().getDescription(i18n);
        assertTrue(aggregateDescription.contains(provider13Id));
        assertTrue(aggregateDescription.contains(provider25Id));
        assertTrue(aggregateDescription.contains(provider4Id));
    }

}