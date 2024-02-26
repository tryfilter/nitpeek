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
    public List<TextPage> renderPages(List<? extends DocxPage> pages) {
        this.pages = List.copyOf(pages);
        var result = new ArrayList<TextPage>(pages.size());

        for (int i = 0; i < pages.size(); i++) {
            result.add(renderPage(pages.get(i), i));
        }

        return List.copyOf(result);
    }

    private TextPage renderPage(DocxPage page, int pageIndex) {
        var segmentRenderer = new DefaultSegmentRenderer(getParagraphRenderer(pageIndex));

        var header = renderSegment(page.getHeader(), segmentRenderer);
        var footer = renderSegment(page.getFooter(), segmentRenderer);
        var footnotes = renderFootnotes(page.getFootnotes(), segmentRenderer);
        var body = segmentRenderer.render(page.getBody());

        var fullPage = Stream.of(header, body, footnotes, footer).flatMap(Collection::stream).toList();

        return new SimpleTextPage(fullPage, pageIndex);
    }

    private List<String> renderSegment(Optional<DocxSegment> segment, SegmentRenderer segmentRenderer) {
        return segment.map(segmentRenderer::render).orElse(List.of());
    }

    private List<String> renderFootnotes(SortedMap<Integer, DocxSegment> footnotes, SegmentRenderer renderer) {
        var result = new ArrayList<String>(footnotes.size());

        for (var footnoteKeyValuePair : footnotes.entrySet()) {
            var footnoteSegment = footnoteKeyValuePair.getValue();
            List<String> lines = renderer.render(footnoteSegment);
            if (!lines.isEmpty()) result.addAll(lines);
        }
        return result;
    }

    private ParagraphRenderer getParagraphRenderer(int pageIndex) {
        return new SimpleParagraphRenderer(
                pageIndex,
                pages.size(),
                new SimpleArabicNumberRenderer()
        );
    }
}
