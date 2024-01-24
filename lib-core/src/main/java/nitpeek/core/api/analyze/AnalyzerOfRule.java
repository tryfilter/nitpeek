package nitpeek.core.api.analyze;

public record AnalyzerOfRule(Analyzer analyzer, RuleType ruleType) {

    public static AnalyzerOfRule createFrom(Rule rule) {
        return new AnalyzerOfRule(rule.createAnalyzer(), rule.getType());
    }
}
