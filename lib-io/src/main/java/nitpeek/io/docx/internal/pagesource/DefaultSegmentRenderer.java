package nitpeek.io.docx.internal.pagesource;

import org.docx4j.wml.P;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

final class DefaultSegmentRenderer implements SegmentRenderer {

    private final ParagraphRenderer paragraphRenderer;

    public DefaultSegmentRenderer(ParagraphRenderer paragraphRenderer) {
        this.paragraphRenderer = paragraphRenderer;
    }

    /**
     * @return a modifiable list
     */
    @Override
    public List<String> render(DocxSegment segment) {

        var paragraphs = segment.paragraphs();
        if (paragraphs.isEmpty()) return new ArrayList<>();

        var firstParagraph = paragraphs.getFirst();
        var lastParagraph = paragraphs.getLast();
        if (firstParagraph == lastParagraph)
            return nonEmpty(renderParagraph(firstParagraph, segment.indexOfFirstRun(), segment.indexOfLastRun()));

        var result = new ArrayList<String>(paragraphs.size());
        result.addAll(nonEmpty(renderParagraph(firstParagraph, segment.indexOfFirstRun(), null)));
        result.addAll(nonEmpty(renderFull(getMiddleParagraphs(paragraphs))));
        result.addAll(nonEmpty(renderParagraph(lastParagraph, null, segment.indexOfLastRun())));

        return result;
    }

    private List<String> nonEmpty(List<String> lines) {
        // We want the returned list to be modifiable, so we collect it into an ArrayList
        return lines.stream().filter(line -> !line.isEmpty()).collect(Collectors.toCollection(ArrayList::new));
    }


    private List<String> renderParagraph(P paragraph, Integer first, Integer last) {
        if (first != null && last != null) return List.of(paragraphRenderer.renderBetween(first, last, paragraph));
        if (first != null) return List.of(paragraphRenderer.renderFrom(first, paragraph));
        if (last != null) return List.of(paragraphRenderer.renderTo(last, paragraph));
        return List.of(paragraphRenderer.render(paragraph));
    }

    private List<String> renderFull(List<P> paragraphs) {
        return paragraphs.stream().map(paragraphRenderer::render).toList();
    }

    private List<P> getMiddleParagraphs(List<P> paragraphs) {
        return paragraphs.subList(1, paragraphs.size() - 1);
    }
}
