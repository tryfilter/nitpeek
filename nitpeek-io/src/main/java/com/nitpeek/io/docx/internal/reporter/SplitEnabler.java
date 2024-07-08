package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.io.docx.internal.common.PartialParagraph;
import com.nitpeek.io.docx.internal.common.RunRenderer;
import com.nitpeek.io.docx.internal.common.SimpleDocxSegment;
import com.nitpeek.io.docx.internal.pagesource.DefaultDocxPage;
import com.nitpeek.io.docx.internal.pagesource.SimpleDocxPage;
import com.nitpeek.io.docx.types.*;

import java.util.SortedMap;
import java.util.TreeMap;

public final class SplitEnabler {
    private final RunSplitEnabler runSplitEnabler;

    public SplitEnabler(RunSplitEnabler runSplitEnabler) {
        this.runSplitEnabler = runSplitEnabler;
    }

    public DocxParagraph<SplittableRun> convertParagraph(DocxParagraph<? extends CompositeRun> paragraph, RunRenderer runRenderer) {
        var runs = paragraph.runs();
        var splittableRuns = runs.stream().map(run -> runSplitEnabler.toSplittable(run, runRenderer)).toList();
        return new PartialParagraph<>(splittableRuns);
    }

    public DocxSegment<SplittableRun> convertSegment(DocxSegment<? extends CompositeRun> segment, RunRenderer runRenderer) {
        var paragraphs = segment.paragraphs();
        var splittableParagraphs = paragraphs.stream().map(p -> convertParagraph(p, runRenderer)).toList();
        return new SimpleDocxSegment<>(splittableParagraphs);
    }

    public SegmentedDocxPage<SplittableRun> convertPage(DocxPage<? extends CompositeRun> originalPage, RunRenderer runRenderer) {
        return new DefaultDocxPage<>(convert(originalPage, runRenderer));
    }

    private DocxPage<SplittableRun> convert(DocxPage<? extends CompositeRun> originalPage, RunRenderer runRenderer) {
        var splittableHeader = originalPage.getHeader().map(s -> convertSegment(s, runRenderer)).orElse(null);
        var splittableBody = convertSegment(originalPage.getBody(), runRenderer);
        var splittableFootnotes = convertFootnotes(originalPage.getFootnotes(), runRenderer);
        var splittableFooter = originalPage.getFooter().map(s -> convertSegment(s, runRenderer)).orElse(null);
        return new SimpleDocxPage<>(splittableHeader, splittableBody, splittableFootnotes, splittableFooter);
    }

    private SortedMap<Integer, DocxSegment<SplittableRun>> convertFootnotes(SortedMap<Integer, ? extends DocxSegment<? extends CompositeRun>> footnotes, RunRenderer runRenderer) {
        var result = new TreeMap<Integer, DocxSegment<SplittableRun>>();
        for (var entry : footnotes.entrySet()) {
            int footnoteNumber = entry.getKey();
            var footnoteSegment = entry.getValue();
            result.put(footnoteNumber, convertSegment(footnoteSegment, runRenderer));
        }
        return result;
    }
}