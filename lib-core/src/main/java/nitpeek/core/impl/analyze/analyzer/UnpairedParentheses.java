package nitpeek.core.impl.analyze.analyzer;

import nitpeek.core.api.analyze.Analyzer;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.impl.common.SimpleFeature;
import nitpeek.core.impl.common.SimpleFeatureComponent;
import nitpeek.core.impl.common.StandardFeature;
import nitpeek.core.internal.Confidence;
import nitpeek.core.api.translate.Translation;

import java.util.*;
import java.util.stream.Stream;

import static nitpeek.core.impl.translate.CoreTranslationKeys.UNPAIRED_CLOSING_PARENTHESIS_COMPONENT_DESCRIPTION;
import static nitpeek.core.impl.translate.CoreTranslationKeys.UNPAIRED_OPEN_PARENTHESIS_COMPONENT_DESCRIPTION;


/**
 * Reports parentheses that are opened but not closed, or closed but not opened.<br>
 * Note that this analyzer does not detect missing parentheses across line boundaries.<br>
 * Note that for identical opening and closing parentheses it is not possible to determine if any particular instance is
 * of the opening or the closing kind. Thus, in such configurations only the last instance is reported, and only if the
 * total number of instances is odd.<br>
 * <br>
 * This analyzer is NOT thread safe.<br>
 * This Analyzer is strongly processing-order independent.
 */
public final class UnpairedParentheses implements Analyzer {

    private final String openParenthesis;
    private final String closeParenthesis;


    private final List<Feature> features = new ArrayList<>();


    public UnpairedParentheses(String openParenthesis, String closeParenthesis) {
        this.openParenthesis = openParenthesis;
        this.closeParenthesis = closeParenthesis;
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
        var lines = page.getLines();
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            addFeatures(line, page, i);
        }
    }

    private void addFeatures(String line, TextPage page, int lineIndex) {
        var openingParentheses = indexes(openParenthesis, line).stream().map(i -> new Parenthesis(i, ParenthesisType.OPEN)).toList();
        var closingParentheses = indexes(closeParenthesis, line).stream().map(i -> new Parenthesis(i, ParenthesisType.CLOSE)).toList();

        if (openParenthesis.equals(closeParenthesis)) {
            if (openingParentheses.size() % 2 == 1)
                addFeatureFor(openingParentheses.getLast(), page, lineIndex);
            return;
        }

        var allParentheses = Stream.of(openingParentheses, closingParentheses)
                .flatMap(List::stream)
                .sorted(Comparator.comparing(Parenthesis::index))
                .toList();

        addFeaturesFor(findUnpairedParentheses(allParentheses), page, lineIndex);
    }

    private List<Parenthesis> findUnpairedParentheses(List<Parenthesis> parentheses) {
        var unpairedParentheses = new ArrayList<Parenthesis>();
        Deque<Parenthesis> openParenthesesStack = new ArrayDeque<>();
        for (var parenthesis : parentheses) {
            saveUnpairedClosingAndPlaceOpeningOnStack(parenthesis, unpairedParentheses, openParenthesesStack);
        }

        unpairedParentheses.addAll(openParenthesesStack);

        return unpairedParentheses;
    }

    private void saveUnpairedClosingAndPlaceOpeningOnStack(Parenthesis parenthesis, List<Parenthesis> unpairedParentheses, Deque<Parenthesis> openParenthesesStack) {
        switch (parenthesis.type) {
            case OPEN -> openParenthesesStack.addLast(parenthesis);
            case CLOSE -> {
                var correspondingOpenParenthesis = openParenthesesStack.pollLast();
                if (correspondingOpenParenthesis == null) unpairedParentheses.add(parenthesis);
            }
        }
    }

    private void addFeaturesFor(List<Parenthesis> parentheses, TextPage page, int lineIndex) {
        for (var parenthesis : parentheses) addFeatureFor(parenthesis, page, lineIndex);
    }

    private void addFeatureFor(Parenthesis parenthesis, TextPage page, int lineIndex) {
        features.add(feature(parenthesis, page, lineIndex));
    }

    private Feature feature(Parenthesis parenthesis, TextPage page, int lineIndex) {
        return new SimpleFeature(
                StandardFeature.UNPAIRED_PARENTHESES.getType(),
                List.of(component(parenthesis, page, lineIndex)),
                Confidence.HIGH.value()
        );
    }

    private FeatureComponent component(Parenthesis parenthesis, TextPage page, int lineIndex) {
        ParenthesisType missingParenthesisType = parenthesis.type.other();
        return new SimpleFeatureComponent(
                i18n -> getTranslation(parenthesisOfType(missingParenthesisType), missingParenthesisType, i18n),
                new TextCoordinate(page.getPageNumber(), lineIndex, parenthesis.index).extendToSelection(parenthesisLength(parenthesis)),
                parenthesisOfType(parenthesis.type)
        );
    }

    private String getTranslation(String missingParenthesis, ParenthesisType missingParenthesisType, Translation translation) {
        if (missingParenthesisType == ParenthesisType.OPEN)
            return translation.translate(UNPAIRED_OPEN_PARENTHESIS_COMPONENT_DESCRIPTION.key(), missingParenthesis);
        else
            return translation.translate(UNPAIRED_CLOSING_PARENTHESIS_COMPONENT_DESCRIPTION.key(), missingParenthesis);
    }

    private int parenthesisLength(Parenthesis parenthesis) {
        return parenthesisOfType(parenthesis.type).length();
    }

    private String parenthesisOfType(ParenthesisType type) {
        return switch (type) {
            case OPEN -> openParenthesis;
            case CLOSE -> closeParenthesis;
        };
    }

    private record Parenthesis(int index, ParenthesisType type) {
    }

    public enum ParenthesisType {
        OPEN, CLOSE;

        public ParenthesisType other() {
            return switch (this) {

                case OPEN -> CLOSE;
                case CLOSE -> OPEN;
            };
        }
    }

    private List<Integer> indexes(String searchTerm, String searchSpace) {
        var result = new ArrayList<Integer>();

        int nextIndex = searchSpace.indexOf(searchTerm);
        while (nextIndex != -1) {
            result.add(nextIndex);
            nextIndex = searchSpace.indexOf(searchTerm, nextIndex + 1);
        }

        return result;
    }
}