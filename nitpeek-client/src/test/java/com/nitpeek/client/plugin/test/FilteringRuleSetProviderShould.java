package com.nitpeek.client.plugin.test;

import com.nitpeek.core.impl.process.FilteringRuleSetProvider;
import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.process.RuleSetProvider;
import com.nitpeek.core.api.process.RuleSetTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.function.Predicate;

import static com.nitpeek.core.impl.process.RuleSetProviderFilters.matchesAllTags;
import static com.nitpeek.core.impl.process.RuleSetProviderFilters.matchesAnyTags;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class FilteringRuleSetProviderShould {
    @Mock private Rule rule1, rule2, rule3, rule4, rule5;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) private RuleSetProvider provider13, provider25, provider4;

    private Set<RuleSetProvider> allProviders;
    private static final String category = "Test";

    private final RuleSetTag TAG_EVEN = new RuleSetTag(category, "Even", "Marks rule sets that have one or more even-numbered rules");
    private final RuleSetTag TAG_ODD = new RuleSetTag(category, "Odd", "Marks rule sets that have one or more odd-numbered rules");
    private final RuleSetTag TAG_PRIME = new RuleSetTag(category, "Prime", "Marks rule sets that have one or more prime-numbered rules");
    private final RuleSetTag TAG_SQUARE = new RuleSetTag(category, "Square", "Marks rule sets that have one or more square-numbered rules");

    @BeforeEach
    void setup() {
        when(provider13.getRules()).thenReturn(Set.of(rule1, rule3));
        when(provider25.getRules()).thenReturn(Set.of(rule2, rule5));
        when(provider4.getRules()).thenReturn(Set.of(rule4));

        when(provider13.getTags()).thenReturn(Set.of(TAG_ODD, TAG_PRIME, TAG_SQUARE));
        when(provider25.getTags()).thenReturn(Set.of(TAG_ODD, TAG_EVEN, TAG_PRIME));
        when(provider4.getTags()).thenReturn(Set.of(TAG_EVEN, TAG_SQUARE));

        allProviders = Set.of(provider13, provider25, provider4);

    }

    @Test
    void returnAllRulesWithAllPassFilter() {
        var allRules = Set.of(rule1, rule2, rule3, rule4, rule5);
        Predicate<RuleSetProvider> allPassFilter = (RuleSetProvider rsp) -> true;
        var filteringRuleSetProvider = new FilteringRuleSetProvider(allProviders, allPassFilter);
        assertEquals(allRules, filteringRuleSetProvider.getRules());
    }

    @Test
    void returnRulesSatisfyingAllTagsFilterOdd() {
        var filteringRuleSetProvider = new FilteringRuleSetProvider(allProviders, rsp -> matchesAllTags(rsp, Set.of(TAG_ODD)));
        var rulesFromOddProviders = Set.of(rule1,  rule3, rule2, rule5);
        assertEquals(rulesFromOddProviders, filteringRuleSetProvider.getRules());
    }

    @Test
    void returnRulesSatisfyingAllTagsFilterEven() {

        var filteringRuleSetProvider = new FilteringRuleSetProvider(allProviders, rsp -> matchesAllTags(rsp, Set.of(TAG_EVEN)));
        var rulesFromEvenProviders = Set.of(rule2, rule5, rule4);
        assertEquals(rulesFromEvenProviders, filteringRuleSetProvider.getRules());
    }

    @Test
    void returnRulesSatisfyingAllTagsFilterOddAndEven() {

        var filteringRuleSetProvider = new FilteringRuleSetProvider(allProviders, rsp -> matchesAllTags(rsp, Set.of(TAG_ODD, TAG_EVEN)));
        var rulesFromEvenAndOddProviders = Set.of(rule2, rule5);
        assertEquals(rulesFromEvenAndOddProviders, filteringRuleSetProvider.getRules());
    }

    @Test
    void returnRulesSatisfyingAllTagsFilterPrimeAndSquare() {

        var filteringRuleSetProvider = new FilteringRuleSetProvider(allProviders, rsp -> matchesAllTags(rsp, Set.of(TAG_PRIME, TAG_SQUARE)));
        var rulesFromPrimeAndSquareProviders = Set.of(rule1, rule3);
        assertEquals(rulesFromPrimeAndSquareProviders, filteringRuleSetProvider.getRules());
    }

    @Test
    void returnRulesSatisfyingAnyTagsFilterOddOrEven() {

        var filteringRuleSetProvider = new FilteringRuleSetProvider(allProviders, rsp -> matchesAnyTags(rsp, Set.of(TAG_ODD, TAG_EVEN)));
        var allRules = Set.of(rule1, rule2, rule3, rule4, rule5);
        assertEquals(allRules, filteringRuleSetProvider.getRules());
    }

    @Test
    void returnRulesSatisfyingAnyTagsFilterOddOrSquare() {

        var filteringRuleSetProvider = new FilteringRuleSetProvider(allProviders, rsp -> matchesAnyTags(rsp, Set.of(TAG_ODD, TAG_EVEN)));
        var allRules = Set.of(rule1, rule2, rule3, rule4, rule5);
        assertEquals(allRules, filteringRuleSetProvider.getRules());
    }

    @Test
    void returnRulesSatisfyingAnyTagsFilterOddOrPrime() {

        var filteringRuleSetProvider = new FilteringRuleSetProvider(allProviders, rsp -> matchesAnyTags(rsp, Set.of(TAG_ODD, TAG_PRIME)));
        var oddOrPrimeRules = Set.of(rule1, rule3, rule2, rule5);
        assertEquals(oddOrPrimeRules, filteringRuleSetProvider.getRules());
    }
}