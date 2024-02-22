package nitpeek.io.docx.internal.pagesource;

import org.docx4j.wml.P;

import java.util.ArrayList;
import java.util.List;

public final class DefaultSegmentRenderer implements SegmentRenderer {

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
            return new ArrayList<>(renderParagraph(firstParagraph, segment.indexOfFirstRun(), segment.indexOfLastRun()));

        var result = new ArrayList<String>(paragraphs.size());
        result.addAll(renderParagraph(firstParagraph, segment.indexOfFirstRun(), null));
        result.addAll(renderFull(getMiddleParagraphs(paragraphs)));
        result.addAll(renderParagraph(lastParagraph, null, segment.indexOfLastRun()));

        return result;
    }


    private List<String> renderParagraph(P paragraph, Integer first, Integer last) {
        if (emptyRange(first, last, DocxUtil.getRuns(paragraph).size())) return List.of();
        if (first != null && last != null) return List.of(paragraphRenderer.renderBetween(first, last, paragraph));
        if (first != null) return List.of(paragraphRenderer.renderFrom(first, paragraph));
        if (last != null) return List.of(paragraphRenderer.renderTo(last, paragraph));
        return List.of(paragraphRenderer.render(paragraph));
    }

    private boolean emptyRange(Integer first, Integer last, int size) {
        if (first != null && first >= size) return true;
        if (last != null && last < 0) return true;
        return first != null && last != null && first > last;
    }

    private List<String> renderFull(List<P> paragraphs) {
        return paragraphs.stream().map(paragraphRenderer::render).toList();
    }

    private List<P> getMiddleParagraphs(List<P> paragraphs) {
        return paragraphs.subList(1, paragraphs.size() - 1);
    }
}
