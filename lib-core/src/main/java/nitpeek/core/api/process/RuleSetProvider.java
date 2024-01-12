package nitpeek.core.api.process;

import nitpeek.core.api.analyze.Rule;

import java.util.Set;

public interface RuleSetProvider {
    Set<Rule> getRules();
}
