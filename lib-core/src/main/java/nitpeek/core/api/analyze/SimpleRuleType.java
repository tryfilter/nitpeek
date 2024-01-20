package nitpeek.core.api.analyze;

import nitpeek.core.api.common.Identifier;
import nitpeek.core.api.common.SimpleIdentifier;

public record SimpleRuleType(String id, String name, String description) implements RuleType {
    @Override
    public Identifier getRuleId() {
        return new SimpleIdentifier(id, name, description);
    }
}
