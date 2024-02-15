package nitpeek.io.docx.internal.pagesource;

import org.docx4j.wml.CTFootnotes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class DefaultFootnotesRenderer implements FootnotesRenderer {

    private final ParagraphRenderer paragraphRenderer;
    private final CTFootnotes footnotes;

    public DefaultFootnotesRenderer(ParagraphRenderer paragraphRenderer, CTFootnotes footnotes) {
        this.paragraphRenderer = paragraphRenderer;
        this.footnotes = footnotes;
    }

    /**
     * @return an unmodifiable copy
     */
    @Override
    public List<String> renderFootnotes(Set<Integer> footnotesToRender) {
        if (footnotes == null) return List.of();
        var relevantFootnotes = footnotes.getFootnote()
                .stream()
                .filter(f -> f.getType() == null) // Word adds some meta-elements among the footnotes; "real" footnotes don't have a Type
                .filter(f -> footnotesToRender.contains(f.getId().intValue()))
                .toList();

        if (relevantFootnotes.isEmpty()) return List.of();
        var contentRenderer = new SimpleContentRenderer(paragraphRenderer);

        var result = new ArrayList<String>(relevantFootnotes.size());

        for (var footnote : relevantFootnotes) {

            List<String> paragraphs = contentRenderer.renderParagraphs(footnote.getContent());
            if (paragraphs.isEmpty()) continue;

            result.add(footnote.getId() + " " + paragraphs.getFirst());
            result.addAll(paragraphs.subList(1, paragraphs.size()));
        }

        return List.copyOf(result);
    }
}
