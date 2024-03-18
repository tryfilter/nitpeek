package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.process.StandardRuleSetTags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class BodyFootnotesRuleSetPartitionerShould {

    @Mock private Rule rule1, rule2, rule3, rule4, rule5, rule6, rule7, rule8, rule9;

    @Mock private RuleSetProvider provider13NoTags, provider25TagBody, provider4TagBodyFootnotes, provider6TagFootnotes, provider7TagAnyAll, provider89AllTags;

    private BodyFootnotesRuleSetPartitioner partitioner;

    @BeforeEach
    void setup() {
        // rule sets are intentionally disjoint: if we see a rule in a result, we know which rule set it came from
        when(provider13NoTags.getRules()).thenReturn(Set.of(rule1, rule3));
        when(provider25TagBody.getRules()).thenReturn(Set.of(rule2, rule5));
        when(provider4TagBodyFootnotes.getRules()).thenReturn(Set.of(rule4));
        when(provider6TagFootnotes.getRules()).thenReturn(Set.of(rule6));
        when(provider7TagAnyAll.getRules()).thenReturn(Set.of(rule7));
        when(provider89AllTags.getRules()).thenReturn(Set.of(rule8, rule9));

        when(provider13NoTags.getTags()).thenReturn(Set.of());
        when(provider25TagBody.getTags()).thenReturn(Set.of(StandardRuleSetTags.contentBody()));
        when(provider4TagBodyFootnotes.getTags()).thenReturn(Set.of(StandardRuleSetTags.contentBody(), StandardRuleSetTags.contentFootnotes()));
        when(provider6TagFootnotes.getTags()).thenReturn(Set.of(StandardRuleSetTags.contentFootnotes()));
        when(provider7TagAnyAll.getTags()).thenReturn(Set.of(StandardRuleSetTags.contentAny()));
        when(provider89AllTags.getTags()).thenReturn(Set.of(StandardRuleSetTags.contentBody(), StandardRuleSetTags.contentFootnotes(), StandardRuleSetTags.contentAny()));

        Set<RuleSetProvider> allProviders = Set.of(provider13NoTags, provider25TagBody, provider4TagBodyFootnotes, provider6TagFootnotes, provider7TagAnyAll, provider89AllTags);
        partitioner = new BodyFootnotesRuleSetPartitioner(allProviders);
    }

    @Test
    void partitionRulesFromUntaggedRuleSetsToUniversallyApplicable() {
        assertTrue(partitioner.rulesApplicableUniversally().containsAll(provider13NoTags.getRules()));
    }

    @Test
    void partitionRulesFromAnyAllTaggedRuleSetsToUniversallyApplicable() {
        var expectedRules = rulesFromProviders(provider7TagAnyAll, provider89AllTags);
        assertTrue(partitioner.rulesApplicableUniversally().containsAll(expectedRules));
    }

    @Test
    void partitionRulesFromBodyButNotAnyAllTaggedRuleSetsToBody() {
        var expectedRules = rulesFromProviders(provider25TagBody, provider4TagBodyFootnotes);
        assertEquals(expectedRules, partitioner.rulesApplicableToBody());
    }

    @Test
    void partitionRulesFromFootnotesButNotAnyAllTaggedRuleSetsToFootnotes() {
        var expectedRules = rulesFromProviders(provider6TagFootnotes, provider4TagBodyFootnotes);
        assertEquals(expectedRules, partitioner.rulesApplicableToFootnotes());
    }

    @Test
    void keepUniversallyApplicablePartitionDisjointFromOtherPartitions() {
        var universallyApplicableRules = partitioner.rulesApplicableUniversally();
        var bodyApplicableRules = partitioner.rulesApplicableToBody();
        var footnoteApplicableRules = partitioner.rulesApplicableToFootnotes();
        assertFalse(bodyApplicableRules.stream().anyMatch(universallyApplicableRules::contains));
        assertFalse(footnoteApplicableRules.stream().anyMatch(universallyApplicableRules::contains));
    }

    private static Set<Rule> rulesFromProviders(RuleSetProvider... providers) {
        return Arrays.stream(providers)
                .map(RuleSetProvider::getRules)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableSet());
    }
}