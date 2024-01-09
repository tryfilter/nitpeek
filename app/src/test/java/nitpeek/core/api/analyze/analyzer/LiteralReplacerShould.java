package nitpeek.core.api.analyze.analyzer;

final class LiteralReplacerShould implements LiteralReplacersShould {

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
        replacerCaseSensitive = new LiteralReplacer(oldValue, newValue, false);
        replacerIgnoreCase = new LiteralReplacer(oldValue, newValue, true);
    }
}