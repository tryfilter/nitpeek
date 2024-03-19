package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.process.StandardRuleSetTags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
final class LanguageRuleSetFilterShould {

    @Mock private RuleSetProvider providerNoTags, providerAnyLanguage, providerTagsOtherCategory, providerEnglishAndGerman, providerEnglish, providerGerman;

    private Set<RuleSetProvider> allProviders;

    @BeforeEach
    void setup() {
        when(providerNoTags.getTags()).thenReturn(Set.of());
        when(providerAnyLanguage.getTags()).thenReturn(Set.of(StandardRuleSetTags.languageAny()));
        when(providerTagsOtherCategory.getTags()).thenReturn(Set.of(StandardRuleSetTags.contentAny(), StandardRuleSetTags.contentFootnotes()));
        when(providerEnglishAndGerman.getTags()).thenReturn(Set.of(StandardRuleSetTags.languageEnglish(), StandardRuleSetTags.languageGerman()));
        when(providerEnglish.getTags()).thenReturn(Set.of(StandardRuleSetTags.languageEnglish()));
        when(providerGerman.getTags()).thenReturn(Set.of(StandardRuleSetTags.languageGerman()));

        allProviders = Set.of(providerNoTags, providerAnyLanguage, providerTagsOtherCategory, providerEnglishAndGerman, providerEnglish, providerGerman);
    }

    @ParameterizedTest
    @MethodSource("languages")
    void alwaysKeepRuleSetsNotTaggedWithAnyLanguageTag(Language language) {
        var filter = new LanguageRuleSetFilter(language);
        var remainingProviders = filter.filter(allProviders);
        assertTrue(remainingProviders.containsAll(Set.of(providerNoTags, providerTagsOtherCategory)));
    }

    @ParameterizedTest
    @MethodSource("languages")
    void alwaysKeepRuleSetsTaggedWithAny(Language language) {
        var filter = new LanguageRuleSetFilter(language);
        var remainingProviders = filter.filter(allProviders);
        assertTrue(remainingProviders.contains(providerAnyLanguage));
    }

    private static Set<Language> languages() {
        return Set.of(Language.values());
    }

    @Test
    void keepRuleSetsUsableWithEnglish() {
        var filter = new LanguageRuleSetFilter(Language.ENGLISH);
        var remainingProviders = filter.filter(allProviders);
        assertEquals(
                Set.of(
                        providerNoTags,
                        providerAnyLanguage,
                        providerTagsOtherCategory,
                        providerEnglishAndGerman,
                        providerEnglish
                ),
                remainingProviders
        );
    }

    @Test
    void keepRuleSetsUsableWithGerman() {
        var filter = new LanguageRuleSetFilter(Language.GERMAN);
        var remainingProviders = filter.filter(allProviders);
        assertEquals(
                Set.of(
                        providerNoTags,
                        providerAnyLanguage,
                        providerTagsOtherCategory,
                        providerEnglishAndGerman,
                        providerGerman
                ),
                remainingProviders
        );
    }
}