package nitpeek.core.api.analyze.transformer;

import nitpeek.core.api.analyze.SimpleTextPage;
import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.TextCoordinate;

import java.util.*;
import java.util.function.Predicate;

/**
 * This transformer joins hyphenated words, defined as an uninterrupted string of word-characters followed by the hyphen symbol,
 * making up the end of a line (the first half) and an uninterrupted string of word-characters making up the beginning of
 * the next line (the second half).<br>
 * By default, word-characters are defined to be any non-whitespace characters, but this can be configured through the constructor.<br>
 * The default hyphen symbol is {@code -}. This also can be configured. <br>
 * <br>
 * This transformer is state-FUL: repeated calls with same page number but different contents may lead to faulty behavior!
 */
public final class HyphenJoiner implements Transformer {

    private final String hyphenSymbol;
    private final Predicate<Character> isWordChar;

    private final Map<Integer, LinesToValues> originalLineLengths = new HashMap<>();
    private final Map<Integer, LinesToValues> characterOffsets = new HashMap<>();

    private final FeatureTransformer transformer = FeatureCoordinatesTransformer.fromCoordinateTransform(this::transformCoordinate);


    private static final class LinesToValues {

        private final Map<Integer, Integer> lineToValue = new HashMap<>();

        public Optional<Integer> valueForLine(int lineNumber) {
            return Optional.ofNullable(lineToValue.get(lineNumber));
        }

        public void putValueForLine(int lineNumber, int value) {
            lineToValue.put(lineNumber, value);
        }
    }

    HyphenJoiner() {
        this("-");
    }

    public HyphenJoiner(String hyphenSymbol) {
        this(hyphenSymbol, (character -> !Character.isWhitespace(character)));
    }

    public HyphenJoiner(Predicate<Character> isWordChar) {
        this("-", isWordChar);
    }

    public HyphenJoiner(String hyphenSymbol, Predicate<Character> isWordChar) {
        this.hyphenSymbol = hyphenSymbol;
        this.isWordChar = isWordChar;
    }

    @Override
    public TextPage transformPage(TextPage original) {
        return new SimpleTextPage(transformLines(original), original.getPageNumber());
    }

    private List<String> transformLines(TextPage original) {
        List<String> lines = original.getLines();
        var transformedLines = new ArrayList<String>(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            var transformedLine = transformLineAndSaveReconstructionInfo(original, i);
            if (transformedLine != null) transformedLines.add(transformedLine);
        }

        return transformedLines;
    }


    private String transformLineAndSaveReconstructionInfo(TextPage page, int lineNumber) {

        List<String> lines = page.getLines();
        String line = lines.get(lineNumber);

        boolean previousLineWasHyphenated = lineNumber > 0 && isHyphenTerminated(lines.get(lineNumber - 1));
        int endOfHyphenContinuation = hyphenContinuationEnd(line, previousLineWasHyphenated);

        saveReconstructionInfo(page, lineNumber, endOfHyphenContinuation);

        if (endOfHyphenContinuation == line.length()) return null;

        String trimmedLine = line.substring(endOfHyphenContinuation);
        if (!isHyphenTerminated(trimmedLine) || lineNumber == lines.size() - 1) return trimmedLine;

        return appendFullLengthHyphenatedLines(lineNumber, trimmedLine, lines);
    }

    // Instead of saving the entire page and extracting reconstruction info (the information required for correctly
    // translating features to the context of the original page) for each line on the fly, we save the info in the form
    // most useful to reconstruction.
    // This both saves memory and prevents duplicating the logic for extracting the info
    // (as we need to extract it, at least in part, for page transformation anyway).
    private void saveReconstructionInfo(TextPage page, int lineNumber, int offset) {
        saveCharacterOffset(page, lineNumber, offset);
        saveLineLength(page, lineNumber);
    }

    private void saveLineLength(TextPage page, int lineNumber) {
        var linesToOriginalLineLengths = originalLineLengths.computeIfAbsent(page.getPageNumber(), p -> new LinesToValues());
        String line = page.getLines().get(lineNumber);
        linesToOriginalLineLengths.putValueForLine(lineNumber,
                line.endsWith(hyphenSymbol) ? line.length() - hyphenSymbol.length() : line.length()
        );
    }

    private void saveCharacterOffset(TextPage page, int lineNumber, int offset) {
        if (offset <= 0) return;

        var linesToCharacterOffsets = characterOffsets.computeIfAbsent(page.getPageNumber(), p -> new LinesToValues());
        linesToCharacterOffsets.putValueForLine(lineNumber, offset);
    }

    private String appendFullLengthHyphenatedLines(int lineNumber, String startTrimmedLine, List<String> lines) {

        var result = new StringBuilder(startTrimmedLine.substring(0, startTrimmedLine.length() - 1));

        int currentLineNumber = lineNumber + 1;
        while (currentLineNumber < lines.size()) {
            String nextLine = lines.get(currentLineNumber);
            int hyphenContinuationEnd = hyphenContinuationEnd(nextLine, true);
            String hyphenContinuation = nextLine.substring(0, hyphenContinuationEnd);
            result.append(hyphenContinuation);

            // keep joining the next line as long as it ends in hyphen and contains only word-characters
            // this enables joining of multi-line hyphenated sections
            if (hyphenContinuationEnd != nextLine.length() || !isHyphenTerminated(hyphenContinuation)) break;

            // remove any superfluous hyphens from the result
            result.delete(result.length() - hyphenSymbol.length(), result.length());

            ++currentLineNumber;
        }
        return result.toString();
    }


    private int hyphenContinuationEnd(String line, boolean wasPreviousLineHyphenated) {
        if (!wasPreviousLineHyphenated) return 0;

        for (int index = 0; index < line.length(); index++) {
            if (!isWordChar.test(line.charAt(index))) return index;
        }
        return line.length();
    }

    private boolean isHyphenTerminated(String line) {
        return line.endsWith(hyphenSymbol);
    }

    @Override
    public Feature transformFeature(Feature original) {
        return transformer.transform(original);
    }

    private TextCoordinate transformCoordinate(TextCoordinate textCoordinate) {
        var linesToLineLengths = originalLineLengths.get(textCoordinate.page());

        if (linesToLineLengths == null)
            throw new IllegalArgumentException("Cannot transform coordinate because relevant page has not been processed yet.");
        var adjustedCoordinate = withTranslatedCharacterIndex(textCoordinate);

        return adjustToOriginalLineLengths(adjustedCoordinate, linesToLineLengths);
    }

    private TextCoordinate withTranslatedCharacterIndex(TextCoordinate textCoordinate) {
        var linesToCharacterOffsets = characterOffsets.get(textCoordinate.page());
        return new TextCoordinate(
                textCoordinate.page(),
                textCoordinate.line(),
                textCoordinate.character() + linesToCharacterOffsets.valueForLine(textCoordinate.line()).orElse(0)
        );
    }

    private static TextCoordinate adjustToOriginalLineLengths(TextCoordinate adjustedCoordinate, LinesToValues linesToLineLengths) {
        int nextLineIndex = adjustedCoordinate.line();
        int remainingLength = adjustedCoordinate.character();
        while (remainingLength >= 0) {
            int nextLineLength = linesToLineLengths.valueForLine(nextLineIndex).orElse(remainingLength + 1);
            if (remainingLength < nextLineLength) {
                return new TextCoordinate(adjustedCoordinate.page(), nextLineIndex, remainingLength);
            }
            remainingLength -= nextLineLength;
            ++nextLineIndex;
        }
        return adjustedCoordinate;
    }
}
