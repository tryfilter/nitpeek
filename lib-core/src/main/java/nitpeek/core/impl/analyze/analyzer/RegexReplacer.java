package nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.api.common.*;
import nitpeek.core.impl.common.SimpleFeature;
import nitpeek.core.impl.common.SimpleFeatureComponent;
import nitpeek.core.impl.common.SimpleFeatureType;
import nitpeek.core.impl.common.StandardFeature;
import nitpeek.core.internal.Confidence;
import nitpeek.core.impl.translate.SimpleDefaultEnglishTranslation;
import nitpeek.core.api.translate.Translation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static nitpeek.core.impl.translate.CoreTranslationKeys.REPLACE_LITERAL_COMPONENT_DESCRIPTION;

/**
 * Reports values matching a regular expression that should be replaced with some other value.<br>
 * Note that this analyzer does not detect values that cross line boundaries.
 * <br>
 * This analyzer is NOT thread safe.<br>
 * This analyzer is strongly processing-order independent.
 */
public final class RegexReplacer implements Analyzer {

    private final Pattern pattern;
    private final String replacement;

    private final SimpleFeatureType reportedSimpleFeatureType;

    private final List<Feature> features = new ArrayList<>();

    public RegexReplacer(Pattern pattern, String replacement) {
        this(pattern, replacement, StandardFeature.REPLACE_REGEX.getType());
    }

    public RegexReplacer(Pattern pattern, String replacement, SimpleFeatureType reportedSimpleFeatureType) {
        this.pattern = pattern;
        this.replacement = replacement;
        this.reportedSimpleFeatureType = reportedSimpleFeatureType;
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
                reportedSimpleFeatureType,
                List.of(component),
                Confidence.HIGH.value()
        );
    }

    private List<FeatureComponent> getReplacements(String textSection, TextPage page, int lineNumber) {
        List<FeatureComponent> result = new ArrayList<>();
        final int pageNumber = page.getPageNumber();

        var matcher = pattern.matcher(textSection);

        while (matcher.find()) {
            int matchLength = matcher.end() - matcher.start();
            if (matchLength <= 0) continue;
            var coordinates = new TextCoordinate(pageNumber, lineNumber, matcher.start()).extendToSelection(matchLength);

            result.add(component(coordinates, replacementWithGroupReferencesApplied(matcher.toMatchResult()), matcher.group()));
        }
        return result;
    }

    private String replacementWithGroupReferencesApplied(MatchResult matchResult) {
        String result = replacement;
        for (int i = matchResult.groupCount(); i >= 1; i--) {
            var group = matchResult.group(i);
            if (group != null) result = result.replaceAll(Pattern.quote("$" + i), matchResult.group(i));
        }
        return result;
    }


    private FeatureComponent component(TextSelection textSelection, String newValue, String oldValue) {
        return new SimpleFeatureComponent(
                i18n -> i18n.translate(REPLACE_LITERAL_COMPONENT_DESCRIPTION.key(), newValue),
                textSelection,
                oldValue
        );
    }
}
