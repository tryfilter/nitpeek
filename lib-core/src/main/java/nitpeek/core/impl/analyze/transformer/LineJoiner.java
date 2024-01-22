package nitpeek.core.impl.analyze.transformer;

import nitpeek.core.impl.analyze.SimpleTextPage;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextPage;
import nitpeek.core.api.util.FeatureTransformer;
import nitpeek.core.api.util.Transformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Makes an entire page look like a page consisting of a single line to the downstream consumer.<br>
 * Text coordinates of the components of returned features are in relation to the original pages.<br>
 * Feature components that cross line boundaries only reflect this fact in their text coordinates, and not in the
 * relevant text portion.
 */
public final class LineJoiner implements Transformer {

    private final String delimiter;

    private final Map<Integer, TextPage> processedPages = new HashMap<>();
    private final FeatureTransformer transformer = FeatureCoordinatesTransformer.fromCoordinateTransform(this::transformCoordinate);


    /**
     * Creates a LineJoiner that uses the empty string as the delimiter between each pair of consecutive lines
     */
    public LineJoiner() {
        this("");
    }

    /**
     * Creates a LineJoiner that uses {@code delimiter} as the delimiter between each pair of consecutive lines
     *
     * @param delimiter the string to insert between adjacent lines
     */
    public LineJoiner(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public TextPage transformPage(TextPage original) {
        processedPages.put(original.getPageNumber(), original);
        return new SimpleTextPage(List.of(String.join(delimiter, original.getLines())), original.getPageNumber());
    }

    /**
     * @throws IllegalArgumentException when {@code original} has any components with non-zero page or line dimensions in
     *                                  their text coordinate or when feature components begin or end inside the delimiter
     *                                  instead of inside a line in the originally processed page
     * @throws IllegalStateException    when called before calling {@link #transformPage(TextPage)} with the appropriate page
     */
    @Override
    public Feature transformFeature(Feature original) {

        return transformer.transform(original);
    }

    private TextCoordinate transformCoordinate(TextCoordinate coordinateInSingleLinePage) {

        if (coordinateInSingleLinePage.line() != 0)
            throw new IllegalArgumentException("Encountered non-zero line when converting feature passed to LineJoiner." +
                    " LineJoiner expects all features it receives to have text coordinates with a line dimension of 0.");

        if (processedPages.isEmpty())
            throw new IllegalStateException("Cannot transform features when no pages have been processed.");


        int currentCharacter = coordinateInSingleLinePage.character();
        TextPage selectedPage = processedPages.get(coordinateInSingleLinePage.page());

        int selectedLineNumber = 0;
        for (int i = 0; i < selectedPage.getLines().size(); i++) {
            if (currentCharacter < 0)
                throw new IllegalArgumentException("Cannot transform features whose components fall inside the delimiter.");
            selectedLineNumber = i;
            int charactersInLine = selectedPage.getLines().get(i).length();
            if (currentCharacter >= charactersInLine) currentCharacter -= charactersInLine + delimiter.length();
            else break;
        }

        return new TextCoordinate(selectedPage.getPageNumber(), selectedLineNumber, currentCharacter);
    }

}
