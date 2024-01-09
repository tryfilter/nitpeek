package nitpeek.core.api.analyze.analyzer;

import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.common.*;
import nitpeek.core.internal.Confidence;
import nitpeek.translation.DefaultEnglishTranslator;
import nitpeek.translation.Translator;

import java.util.ArrayList;
import java.util.List;

/**
 * Reports literal values that should be replaced with some other value.<br>
 * <br>
 * This analyzer is NOT thread safe.<br>
 * This analyzer is independent of page processing order.<br>
 * <br>
 * Note that this analyzer never detects literal values that cross page boundaries.
 */
public final class LiteralReplacer implements Analyzer {

    private final boolean ignoreCase;
    private final String oldValue;
    private final String newValue;

    private final List<Feature> features = new ArrayList<>();

    private final Translator translator;

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
        this.ignoreCase = ignoreCase;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.translator = translator;
    }

    /**
     * @return an unmodifiable copy
     */
    @Override
    public List<Feature> findFeatures() {
        return List.copyOf(features);
    }

    @Override
    public void processPage(TextPage page) {
        processPageLineByLine(page);
    }

    private void processPageLineByLine(TextPage page) {
        var lines = page.getLines();
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            addFeatures(line, page, i);
        }
    }

    private void addFeatures(String textSection, TextPage page, int lineNumber) {
        var replacementComponents = getReplacements(textSection, page, lineNumber);
        var featuresStream = replacementComponents.stream().map(this::wrapComponent);
        features.addAll(featuresStream.toList());
    }

    private Feature wrapComponent(FeatureComponent component) {
        return new SimpleFeature(
                StandardFeature.REPLACE_LITERAL.getType(translator),
                List.of(component),
                Confidence.HIGH.value()
        );
    }

    private List<FeatureComponent> getReplacements(String textSection, TextPage page, int lineNumber) {
        List<FeatureComponent> result = new ArrayList<>();
        final int pageNumber = page.getPageNumber();
        int currentIndex = 0;

        var searchContext = ignoreCaseIfNeeded(textSection);
        var searchTerm = ignoreCaseIfNeeded(oldValue);
        while (true) {
            currentIndex = searchContext.indexOf(searchTerm, currentIndex);
            if (currentIndex < 0) break;
            result.add(component(sameLine(pageNumber, lineNumber, currentIndex)));
            currentIndex += oldValue.length();
        }
        return result;
    }

    private String ignoreCaseIfNeeded(String value) {
        if (ignoreCase)
            return value.toUpperCase();
        return value;
    }

    private TextSelection sameLine(int pageNumber, int lineNumber, int matchIndex) {
        return new TextCoordinate(pageNumber, lineNumber, matchIndex).extendToSelection(oldValue.length());
    }


    private FeatureComponent component(TextSelection textSelection) {
        return new SimpleFeatureComponent(translator.replaceLiteralComponentDescription(newValue), textSelection, oldValue);
    }
}
