package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.process.RuleSetProvider;
import nitpeek.core.api.process.RuleSetTag;
import nitpeek.core.api.process.StandardRuleSetTags;

import java.util.Set;
import java.util.stream.Collectors;

import static nitpeek.core.api.process.StandardRuleSetTags.Category;

final class LanguageRuleSetFilter implements RuleSetFilter {

    private final Language language;

    LanguageRuleSetFilter(Language language) {
        this.language = language;
    }

    @Override
    public Set<RuleSetProvider> filter(Set<RuleSetProvider> ruleSetProviders) {
        return ruleSetProviders.stream().filter(this::matchesLanguage).collect(Collectors.toUnmodifiableSet());
    }

    private boolean matchesLanguage(RuleSetProvider ruleSetProvider) {
        var tags = ruleSetProvider.getTags();
        return tags.contains(language.tag()) ||
                tags.contains(StandardRuleSetTags.languageAny()) ||
                RuleSetTag.keepTagsFromCategory(Category.LANGUAGE.getId(), tags).isEmpty();
    }
}
