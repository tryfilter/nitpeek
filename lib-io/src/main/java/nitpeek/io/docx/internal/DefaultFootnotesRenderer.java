package nitpeek.io.docx.internal;

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

    @Override
    public List<String> renderFootnotes(Set<Integer> footnotesToRender) {
        if (footnotes == null) return List.of();
        var relevantFootnotes = footnotes.getFootnote()
                .stream()
                .filter(f -> f.getType() == null) // Word ads some meta-elements among the footnotes; "real" footnotes don't have a Type
                .filter(f -> footnotesToRender.contains(f.getId().intValue()))
                .toList();

        if (relevantFootnotes.isEmpty()) return List.of();
        var contentRenderer = new SimpleContentRenderer(paragraphRenderer);

        var result = new ArrayList<String>(relevantFootnotes.size());

        for (var footnote : relevantFootnotes) {
            // Generally there should only be one paragraph per footnote. If not, we separate the paragraphs belonging to
            // the same footnote by space.
            var footnoteContents = String.join(" ", contentRenderer.renderParagraphs(footnote.getContent()));
            result.add(footnote.getId() + " " + footnoteContents);
        }

        return result;
    }
}
