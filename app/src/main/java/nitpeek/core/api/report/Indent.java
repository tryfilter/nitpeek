package nitpeek.core.api.report;

public class Indent {

    public static final String DEFAULT_INDENT_SYMBOL = " ";

    private final String fullIndent;

    public static Indent DEFAULT() {
        return new Indent(4);
    }

    public Indent(int indentByHowManySymbolsPerLevel) {
        this(indentByHowManySymbolsPerLevel, DEFAULT_INDENT_SYMBOL);
    }
    public Indent(int indentByHowManySymbolsPerLevel, String indentSymbol) {
        this.fullIndent = indentSymbol.repeat(indentByHowManySymbolsPerLevel);
    }


    public String indentContainedLines(String lines) {
        StringBuilder result = new StringBuilder();
        for (var line : lines.lines().toList()) {
            result.append(indentLine(line)).append('\n');
        }
        // remove trailing newline
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    private String indentLine(String line) {
        return fullIndent + line;
    }
}
