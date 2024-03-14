package nitpeek.core.api.process;

import java.util.Objects;

/**
 * No i18n since tags are relevant for developers rather than end users.
 */
public record RuleSetTag(String name, String description) {

    // Only use name in equals & hashCode, because the name also functions as the ID of a tag
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleSetTag that = (RuleSetTag) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
