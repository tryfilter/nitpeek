package com.nitpeek.core.impl.analyze;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.api.analyze.Rule;
import com.nitpeek.core.api.analyze.RuleType;

import java.util.function.Supplier;

public final class SimpleRule implements Rule {

    private final RuleType ruleType;
    private final Supplier<Analyzer> analyzer;

    public SimpleRule(RuleType ruleType, Supplier<Analyzer> analyzer) {
        this.ruleType = ruleType;
        this.analyzer = analyzer;
    }

    @Override
    public RuleType getType() {
        return ruleType;
    }

    @Override
    public Analyzer createAnalyzer() {
        return analyzer.get();
    }
}
