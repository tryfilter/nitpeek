package nitpeek.client.demo;

import nitpeek.client.demo.rule.SampleRule;
import nitpeek.core.impl.common.SimpleIdentifier;
import nitpeek.core.impl.process.SimpleRuleSetProvider;
import nitpeek.core.impl.process.StringPageSource;

import java.util.List;
import java.util.Set;

public final class DemoStdOutProcessor {

    public static void main(String[] args) {
        var simpleRules = new SimpleRuleSetProvider(Set.of(SampleRule.DESCRIBE_PAGE_PROCESSING_INFORMATION),
                new SimpleIdentifier("nitpeek.example.PAGE_PROCESSING", "non-existent key: Example RuleSet", "non-existent key: Simple set of rules for remonstration purposes"));
        var processor = new StandardOutputProcessor(simpleRules);

        var pageSource = new StringPageSource(List.of(
                "-Title-\n--Author--\nPage 1: The beginning...",
                "Page 2: Once upon a time there was\na book with no purpose",
                "Page 3: apart from\nstanding in as a\ntest dummy for\ndemonstration purposes.",
                "Page 4: There is,\nso far,\nnot much else to this book..."
        ));

        processor.startProcessing(pageSource);
    }
}
