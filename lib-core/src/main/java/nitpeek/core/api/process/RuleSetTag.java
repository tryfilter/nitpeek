package nitpeek.core.api.process;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * No i18n since tags are relevant for developers rather than end users.
 */
public record RuleSetTag(String category, String name, String description) {

    public String fullId() {
        return category() + "." + name();
    }

    // Don't use description in equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleSetTag that = (RuleSetTag) o;
        return Objects.equals(name, that.name) && Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category);
    }

    public static Set<RuleSetTag> keepTagsFromCategory(String category, Set<RuleSetTag> tags) {
        return tags.stream().filter(tag -> tag.category().equals(category)).collect(Collectors.toUnmodifiableSet());
    }
}