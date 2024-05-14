package nitpeek.io.docx.internal.pagesource;

import nitpeek.core.api.common.TextCoordinate;
import nitpeek.core.api.config.standard.footnote.FootnoteContent;
import nitpeek.io.docx.internal.common.RunRendererFactory;
import nitpeek.io.docx.internal.pagesource.render.SimpleParagraphRenderer;
import nitpeek.io.docx.internal.pagesource.render.SimpleSegmentRenderer;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.DocxPage;

import java.util.HashMap;
import java.util.Map;

public final class DefaultFootnoteContentExtractor implements FootnoteContentExtractor {

    private final RunRendererFactory runRendererFactory;
    private final int pageCount;

    public DefaultFootnoteContentExtractor(RunRendererFactory runRendererFactory, int pageCount) {
        this.runRendererFactory = runRendererFactory;
        this.pageCount = pageCount;
    }

    @Override
    public Map<Integer, FootnoteContent> extractFootnoteContents(DocxPage<? extends CompositeRun> docxPage, int pageIndex) {
        var result = new HashMap<Integer, FootnoteContent>();
        var footnotes = docxPage.getFootnotes();
        if (footnotes.isEmpty()) return result;

        int linesInPreviousFootnotes = 0;
        var segmentRenderer = new SimpleSegmentRenderer(new SimpleParagraphRenderer(runRendererFactory.createRunRenderer(pageIndex, pageCount)));
        for (int ordinal : footnotes.keySet().stream().sorted().toList()) {
            var footnoteSegment = footnotes.get(ordinal);

            var contentLines = segmentRenderer.render(footnoteSegment);
            var contentSelection = new TextCoordinate(pageIndex, linesInPreviousFootnotes, 0).extendToSelection(contentLines.size(), contentLines.getLast().length());
            result.put(ordinal, new FootnoteContent(contentLines, contentSelection));

            linesInPreviousFootnotes += contentLines.size();
        }

        return result;
    }
}