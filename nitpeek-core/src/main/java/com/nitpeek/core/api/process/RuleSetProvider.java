package com.nitpeek.core.api.process;

import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.common.Identifier;

import java.util.Set;

public interface RuleSetProvider {
    Identifier getRuleSetId();
    Set<Rule> getRules();

    default Set<RuleSetTag> getTags() {
        return Set.of();
    }
}
