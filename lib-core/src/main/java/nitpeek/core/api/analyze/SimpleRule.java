package nitpeek.core.api.analyze;

import nitpeek.core.api.analyze.analyzer.Analyzer;

public final class SimpleRule implements Rule {

    private final RuleType ruleType;
    private final Analyzer analyzer;

    public SimpleRule(RuleType ruleType, Analyzer analyzer) {
        this.ruleType = ruleType;
        this.analyzer = analyzer;
    }

    @Override
    public RuleType getType() {
        return ruleType;
    }

    @Override
    public Analyzer getAnalyzer() {
        return analyzer;
    }
}
