package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.StandardFeature;
import nitpeek.translation.DefaultEnglishTranslator;
import nitpeek.translation.Translator;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Reports literal values that should be replaced with some other value.<br>
 * <br>
 * This analyzer is NOT thread safe.<br>
 * This analyzer is independent of page processing order.<br>
 * <br>
 * Note that this analyzer never detects literal values that cross page boundaries.
 */
public final class LiteralReplacer implements Analyzer {


    private final Analyzer regexReplacer;

    /**
     * Creates a LiteralReplacer that replaces each occurrence of {@code oldValue} with the value {@code newValue}. <br>
     * The search does <i>not</i> cross line boundaries. <br>
     * This is equivalent to calling {@code LiteralReplacer(oldValue, newValue, true)}.
     *
     * @param oldValue the literal value to be replaced; if this contains any new line character sequences,
     *                 no matches will be found
     * @param newValue the value to insert in place of the replaced literal
     */
    public LiteralReplacer(String oldValue, String newValue) {
        this(oldValue, newValue, true);
    }

    /**
     * Creates a LiteralReplacer that replaces each occurrence of {@code oldValue} with the value {@code newValue}. <br>
     * The search does <i>not</i> cross line boundaries. <br>
     * This is equivalent to calling {@code LiteralReplacer(oldValue, newValue, true, translator)}.
     *
     * @param oldValue   the literal value to be replaced; if this contains any new line character sequences,
     *                   no matches will be found
     * @param newValue   the value to insert in place of the replaced literal
     * @param translator the translator to use when describing found features and their components
     */
    public LiteralReplacer(String oldValue, String newValue, Translator translator) {
        this(oldValue, newValue, true, translator);
    }

    /**
     * Creates a LiteralReplacer that replaces each occurrence of {@code oldValue} with the value {@code newValue}. <br>
     * Uses the DefaultEnglishTranslator for internationalization.
     *
     * @param oldValue   the literal value to be replaced
     * @param newValue   the value to insert in place of the replaced literal
     * @param ignoreCase if this flag is true, this analyzer will find instances of {@code oldValue} in the text even when
     *                   their case doesn't match that of {@code oldValue}
     */
    public LiteralReplacer(String oldValue, String newValue, boolean ignoreCase) {
        this(oldValue, newValue, ignoreCase, new DefaultEnglishTranslator());
    }

    /**
     * Creates a LiteralReplacer that replaces each occurrence of {@code oldValue} with the value {@code newValue}. <br>
     *
     * @param oldValue   the literal value to be replaced
     * @param newValue   the value to insert in place of the replaced literal
     * @param ignoreCase if this flag is true, this analyzer will find instances of {@code oldValue} in the text even when
     *                   their case doesn't match that of {@code oldValue}
     * @param translator the translator to use when describing found features and their components
     */
    public LiteralReplacer(String oldValue, String newValue, boolean ignoreCase, Translator translator) {
        Pattern pattern = Pattern.compile(oldValue, computePatternFlags(ignoreCase));
        this.regexReplacer = new RegexReplacer(pattern, newValue, translator, StandardFeature.REPLACE_LITERAL.getType());
    }

    private int computePatternFlags(boolean ignoreCase) {
        int flags = Pattern.LITERAL;
        if (ignoreCase) flags = flags | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        return flags;
    }

    /**
     * @return an unmodifiable copy
     */
    @Override
    public List<Feature> findFeatures() {
        return regexReplacer.findFeatures();
    }

    @Override
    public void processPage(TextPage page) {
        regexReplacer.processPage(page);
    }
}
