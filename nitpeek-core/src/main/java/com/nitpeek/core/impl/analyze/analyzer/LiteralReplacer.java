package com.nitpeek.core.impl.analyze.analyzer;

import com.nitpeek.core.api.analyze.Analyzer;
import com.nitpeek.core.api.common.Feature;
import com.nitpeek.core.api.common.TextPage;
import com.nitpeek.core.impl.common.StandardFeature;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Reports literal values that should be replaced with some other value.<br>
 * Note that this analyzer does not detect literal values that cross line boundaries.<br>
 * <br>
 * This analyzer is NOT thread safe.<br>
 * This Analyzer is strongly processing-order independent.
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
     *
     * @param oldValue   the literal value to be replaced
     * @param newValue   the value to insert in place of the replaced literal
     * @param ignoreCase if this flag is true, this analyzer will find instances of {@code oldValue} in the text even when
     *                   their case doesn't match that of {@code oldValue}
     */
    public LiteralReplacer(String oldValue, String newValue, boolean ignoreCase) {
        Pattern pattern = Pattern.compile(oldValue, computePatternFlags(ignoreCase));
        this.regexReplacer = new RegexReplacer(pattern, newValue, StandardFeature.REPLACE_LITERAL.getType());
    }

    private int computePatternFlags(boolean ignoreCase) {
        int flags = Pattern.LITERAL;
        if (ignoreCase) flags = flags | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
        return flags;
    }

    @Override
    public List<Feature> findFeatures() {
        return regexReplacer.findFeatures();
    }

    @Override
    public void processPage(TextPage page) {
        regexReplacer.processPage(page);
    }
}
