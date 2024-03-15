package nitpeek.io.docx.internal.pagesource.render;

import nitpeek.core.api.common.TextPage;
import nitpeek.core.impl.analyze.SimpleTextPage;
import nitpeek.io.docx.internal.common.DocxSegment;
import nitpeek.io.docx.render.DocxPage;
import nitpeek.io.docx.render.CompositeRun;

import java.util.*;
import java.util.stream.Stream;

public final class DefaultDocxPageRenderer implements DocxPageRenderer {

    private List<? extends DocxPage<? extends CompositeRun>> pages = List.of();

    @Override
    public List<TextPage> renderPages(List<? extends DocxPage<? extends CompositeRun>> pages) {
        this.pages = List.copyOf(pages);
        var result = new ArrayList<TextPage>(pages.size());

        for (int i = 0; i < pages.size(); i++) {
            result.add(renderPage(pages.get(i), i));
        }

        return List.copyOf(result);
    }

    private TextPage renderPage(DocxPage<? extends CompositeRun> page, int pageIndex) {
        var segmentRenderer = new SimpleSegmentRenderer(getParagraphRenderer(pageIndex));

        var header = renderSegment(page.getHeader(), segmentRenderer);
        var footer = renderSegment(page.getFooter(), segmentRenderer);
        var footnotes = renderFootnotes(page.getFootnotes(), segmentRenderer);
        var body = segmentRenderer.render(page.getBody());

        var fullPage = Stream.of(header, body, footnotes, footer).flatMap(Collection::stream).toList();

        return new SimpleTextPage(fullPage, pageIndex);
    }

    private List<String> renderSegment(Optional<? extends DocxSegment<? extends CompositeRun>> segment, SegmentRenderer segmentRenderer) {
        return segment.map(segmentRenderer::render).orElse(List.of());
    }

    private List<String> renderFootnotes(SortedMap<Integer, ? extends DocxSegment<? extends CompositeRun>> footnotes, SegmentRenderer renderer) {
        var result = new ArrayList<String>(footnotes.size());

        for (var footnoteKeyValuePair : footnotes.entrySet()) {
            var footnoteSegment = footnoteKeyValuePair.getValue();
            List<String> lines = renderer.render(footnoteSegment);
            if (!lines.isEmpty()) result.addAll(lines);
        }
        return result;
    }

    private ParagraphRenderer getParagraphRenderer(int pageIndex) {
        return new SimpleParagraphRenderer(new SimpleRunRenderer(
                pageIndex,
                pages.size(),
                new SimpleArabicNumberRenderer())
        );
    }
}
