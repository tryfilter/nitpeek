package nitpeek.io.internal;

import nitpeek.core.api.common.Feature;
import nitpeek.core.api.common.FeatureComponent;
import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.common.TextPage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.round;

public final class SectionExtractor extends PDFTextStripper {

    private final Map<Integer, List<FeatureComponent>> pageToComponentStarts;
    private final Map<FeatureComponent, LocalTextPosition> openSections = new HashMap<>();
    private final List<TextPage> pages;

    private final Map<FeatureComponent, List<Section>> result = new HashMap<>();
    private TextCoordinate wordEndPosition = new TextCoordinate(0, 0, 0);
    private TextCoordinate wordStartPosition = wordEndPosition;
    private int offsetForWordSeparators = 0;

    private TextPosition previousEndTextPosition = null;
    private TextPosition currentStartTextPosition = null;

    public record LocalTextPosition(TextPosition position, int page, int line) {
    }

    public LocalTextPosition positionWithCurrentLocation(TextPosition position) {
        return new LocalTextPosition(position, currentPage(), currentLine());
    }

    public record ComponentWithSections(FeatureComponent component, List<Section> sections) {
    }

    public record Section(LocalTextPosition start, LocalTextPosition end, int page) {
    }

    public SectionExtractor(List<Feature> features, List<TextPage> pages) {
        this.pageToComponentStarts = extractComponentsStartingIn(features);
        this.pages = pages;
        setSortByPosition(true);
    }


    public List<ComponentWithSections> getSections(PDDocument pdf) throws IOException {
        var outputSink = new OutputStreamWriter(OutputStream.nullOutputStream());
        // we don't care about the output, we just need to process the entire PDF
        writeText(pdf, outputSink);

        return result.entrySet().stream().map(featureComponentListEntry -> {
            var featureComponent = featureComponentListEntry.getKey();
            var sections = featureComponentListEntry.getValue();
            return new ComponentWithSections(featureComponent, sections);
        }).toList();
    }

    @Override
    protected void writeWordSeparator() throws IOException {
        super.writeWordSeparator();
        offsetForWordSeparators += getWordSeparator().length();
    }

    // We assume that each string corresponds to a word (separated by whitespace) and that words are processed per line
    // from left to right and that lines are processed from top to bottom
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) {

        currentStartTextPosition = textPositions.getFirst();

        int pageIndex = getCurrentPageNo() - 1;
        var textPage = pages.get(pageIndex);

        wordStartPosition = updateToCurrentPage(wordStartPosition);
        wordEndPosition = wordStartPosition.extendToSelection(string.length()).toInclusive();

        updateSections(textPositions);

        previousEndTextPosition = textPositions.getLast();
        wordStartPosition = updateToCurrentLine(wordStartPosition, textPage, string);

    }

    private void updateSections(List<TextPosition> textPositions) {

        closeAndSaveOpenSectionsFromPreviousLine();
        closeAndSaveSectionsEndingInCurrentWord(textPositions);
        addOpenSectionsForCurrentWord(textPositions);
        closeAndSaveSectionsEndingInCurrentWord(textPositions);
    }

    private void closeAndSaveSectionsEndingInCurrentWord(List<TextPosition> textPositions) {
        var sectionsToClose = openSectionsFiltered(sectionStart -> {
            var end = sectionStart.featureComponent().getCoordinates().toInclusive();
            return wordStartPosition.compareTo(end) <= 0 && end.compareTo(wordEndPosition) <= 0;
        }).toList();
        closeAndSave(sectionsToClose, textPositions);
    }

    private void closeAndSave(List<SectionStart> sections, List<TextPosition> textPositions) {
        for (var sectionStart : sections) {

            var featureComponent = sectionStart.featureComponent();

            // Assume pdfbox writes words without crossing line boundaries
            int offset = featureComponent.getCoordinates().toInclusive().character() - wordStartPosition.character();
            saveSection(sectionStart, textPositions.get(offset));
            openSections.remove(featureComponent);
        }
    }

    private void addOpenSectionsForCurrentWord(List<TextPosition> textPositions) {
        var startingComponents = getComponentsStartingInCurrentWord();
        for (var component : startingComponents) {
            openSections.put(component, positionWithCurrentLocation(textPositions.get(component.getCoordinates().fromInclusive().character())));
        }
    }

    private List<FeatureComponent> getComponentsStartingInCurrentWord() {
        var currentPageIndex = getCurrentPageNo() - 1;
        var featureComponents = pageToComponentStarts.get(currentPageIndex);
        if (featureComponents == null || featureComponents.isEmpty()) return List.of();
        int currentLine = wordEndPosition.line();
        int minChar = wordStartPosition.character();
        int maxChar = wordEndPosition.character();
        return featureComponents.stream().filter(component -> {
            var start = component.getCoordinates().fromInclusive();
            return start.line() == currentLine && start.character() >= minChar && start.character() <= maxChar;
        }).toList();
    }

    private void closeAndSaveOpenSectionsFromPreviousLine() {
        var sectionsToClose = openSectionsFiltered(sectionStart -> {
            var start = sectionStart.featureComponent.getCoordinates().fromInclusive();
            return (sectionStart.startPosition().line() < wordStartPosition.line() || start.page() < wordStartPosition.page());
        }).toList();

        saveAndSplit(sectionsToClose);
    }

    private int currentLine() {
        return wordStartPosition.line();
    }

    private int currentPage() {
        return getCurrentPageNo() - 1;
    }

    private void saveAndSplit(List<SectionStart> sections) {
        for (var sectionStart : sections) {
            saveSectionUpToPreviousEndPos(sectionStart);
            openSections.put(sectionStart.featureComponent(), positionWithCurrentLocation(currentStartTextPosition));
        }
    }

    private void saveSectionUpToPreviousEndPos(SectionStart sectionStart) {
        saveSection(sectionStart, widenToRight(previousEndTextPosition));
    }

    private TextPosition widenToRight(TextPosition p) {
        int widenBy = 20;
        return new TextPosition(
                p.getRotation(),
                p.getPageWidth(),
                p.getPageHeight(),
                p.getTextMatrix(),
                p.getEndX() + widenBy,
                p.getEndY(),
                p.getHeightDir(),
                p.getWidth(),
                p.getWidthOfSpace(),
                p.getUnicode(),
                p.getCharacterCodes(),
                p.getFont(),
                p.getFontSize(),
                round(p.getFontSizeInPt())
        );
    }

    private void saveSection(SectionStart sectionStart, TextPosition endPosition) {

        var startPosition = sectionStart.startPosition();
        int page = startPosition.page();
        int line = startPosition.line();

        result.merge(
                sectionStart.featureComponent(),
                new ArrayList<>(List.of(new Section(startPosition, new LocalTextPosition(endPosition, page, line), page))),
                SectionExtractor::mergeLists
        );
    }

    private static List<Section> mergeLists(List<Section> original, List<Section> append) {

        original.addAll(append);
        return original;
    }


    private Stream<SectionStart> openSectionsFiltered(Predicate<SectionStart> predicate) {
        return openSections.entrySet().stream()
                .map(SectionStart::new)
                .filter(predicate);
    }

    private record SectionStart(FeatureComponent featureComponent, LocalTextPosition startPosition) {
        public SectionStart(Map.Entry<FeatureComponent, LocalTextPosition> entry) {
            this(entry.getKey(), entry.getValue());
        }
    }

    private TextCoordinate updateToCurrentLine(TextCoordinate coordinate, TextPage currentTextPage, String nextWord) {
        var lines = currentTextPage.getLines();
        var updatedCoordinate = new TextCoordinate(coordinate.page(), coordinate.line(), coordinate.character() + nextWord.length());
        if (isCoordinateWithinBounds(updatedCoordinate, lines)) return updatedCoordinate;

        offsetForWordSeparators = 0;
        return new TextCoordinate(updatedCoordinate.page(), updatedCoordinate.line() + 1, 0);
    }

    private boolean isCoordinateWithinBounds(TextCoordinate coordinate, List<String> linesOnPage) {
        return coordinate.character() + offsetForWordSeparators < linesOnPage.get(coordinate.line()).length();
    }

    private TextCoordinate updateToCurrentPage(TextCoordinate coordinate) {
        int pageIndex = getCurrentPageNo() - 1;
        if (pageIndex == coordinate.page()) return coordinate;

        return new TextCoordinate(pageIndex, 0, 0);
    }

    private static Map<Integer, List<FeatureComponent>> extractComponentsStartingIn(List<Feature> features) {
        return features.stream().
                flatMap(feature -> feature.getComponents().stream())
                .collect(Collectors.groupingBy(component -> component.getCoordinates().fromInclusive().page()));
    }
}
