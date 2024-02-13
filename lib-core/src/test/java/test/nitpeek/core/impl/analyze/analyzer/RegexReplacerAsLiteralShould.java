package test.nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.impl.analyze.analyzer.RegexReplacer;
import nitpeek.core.impl.common.StandardFeature;

import java.util.regex.Pattern;

final class RegexReplacerAsLiteralShould implements LiteralReplacersShould {

    private Analyzer replacerCaseSensitive;
    private Analyzer replacerIgnoreCase;


    @Override
    public Analyzer getCaseInsensitiveReplacer() {
        return replacerIgnoreCase;
    }

    @Override
    public Analyzer getCaseSensitiveReplacer() {
        return replacerCaseSensitive;
    }

    @Override
    public void setupReplacers(String oldValue, String newValue) {
        replacerCaseSensitive = new RegexReplacer(getPattern(oldValue, false), newValue, StandardFeature.REPLACE_LITERAL.getType());
        replacerIgnoreCase = new RegexReplacer(getPattern(oldValue, true), newValue, StandardFeature.REPLACE_LITERAL.getType());
    }

    private Pattern getPattern(String value, boolean caseSensitive) {
        return Pattern.compile(Pattern.quote(value), caseSensitive ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0);
    }
}