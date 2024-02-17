package nitpeek.io.docx.internal.pagesource;

import nitpeek.core.api.common.TextPage;
import nitpeek.core.impl.analyze.SimpleTextPage;

import java.util.*;
import java.util.stream.Stream;

public final class DefaultDocxPageRenderer implements DocxPageRenderer {

    private List<DocxPage> pages = List.of();

    /**
     * @return an unmodifiable list
     */
    @Override
    public List<TextPage> renderPages(List<DocxPage> pages) {
        this.pages = List.copyOf(pages);
        var result = new ArrayList<TextPage>(pages.size());

        for (int i = 0; i < pages.size(); i++) {
            result.add(renderPage(pages.get(i), i));
        }

        return List.copyOf(result);
    }

    private TextPage renderPage(DocxPage page, int pageIndex) {
        var segmentRenderer = new DefaultSegmentRenderer(getParagraphRenderer(pageIndex));

        var header = renderSegment(page.header(), segmentRenderer);
        var footer = renderSegment(page.footer(), segmentRenderer);
        var footnotes = renderFootnotes(page.footnotes(), segmentRenderer);
        var body = segmentRenderer.render(page.body());

        var fullPage = Stream.of(header, body, footnotes, footer).flatMap(Collection::stream).toList();

        return new SimpleTextPage(fullPage, pageIndex);
    }

    private List<String> renderSegment(Optional<DocxSegment> segment, SegmentRenderer segmentRenderer) {
        return segment.map(segmentRenderer::render).orElse(List.of());
    }

    private List<String> renderFootnotes(SortedMap<Integer, DocxSegment> footnotes, SegmentRenderer renderer) {
        var result = new ArrayList<String>(footnotes.size());

        for (var footnoteKeyValuePair : footnotes.entrySet()) {
            int footnoteReference = footnoteKeyValuePair.getKey();
            var footnoteSegment = footnoteKeyValuePair.getValue();
            List<String> lines = renderer.render(footnoteSegment);
            if (lines.isEmpty()) continue;

            // Insert footnote number, since it is not present as text in the footnote paragraphs
            lines.set(0, footnoteReference + lines.get(0));
            result.addAll(lines);
        }
        return result;
    }

    private ParagraphRenderer getParagraphRenderer(int pageIndex) {
        return new DefaultParagraphRenderer(
                pageIndex + 1, // pageIndex is 0-based
                pages.size()
        );
    }
}
