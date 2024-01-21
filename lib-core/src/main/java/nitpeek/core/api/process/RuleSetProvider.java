package nitpeek.core.api.process;

import nitpeek.core.api.analyze.Rule;
import nitpeek.core.api.common.Identifier;

import java.util.Set;

public interface RuleSetProvider {
    Identifier getRuleSetId();
    Set<Rule> getRules();
}
