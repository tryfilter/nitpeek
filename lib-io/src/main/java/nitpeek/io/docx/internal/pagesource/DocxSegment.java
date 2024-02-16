package nitpeek.io.docx.internal.pagesource;

import org.docx4j.wml.P;

import java.util.List;

/**
 * Represents a segment of contiguous text, with the extents being defined at the "run" granularity level.
 *
 * @param paragraphs      the P elements whose contents constitute this segment
 * @param indexOfFirstRun defines for the first paragraph in {@link #paragraphs} the first run that is part of this segment
 * @param indexOfLastRun  defines for the last paragraph in {@link #paragraphs} the last run that is part of this segment
 */
public record DocxSegment(List<P> paragraphs, int indexOfFirstRun, int indexOfLastRun) {
    public DocxSegment {
        paragraphs = List.copyOf(paragraphs);
    }

    public DocxSegment(List<P> paragraphs) {
        this(paragraphs, 0, getIndexOfLastRun(paragraphs));
    }

    private static int getIndexOfLastRun(List<P> paragraphs) {
        if (paragraphs.isEmpty()) return -1;
        var lastParagraph = paragraphs.getLast();
        var runsLastParagraph = DocxUtil.getRuns(lastParagraph);
        return runsLastParagraph.size() - 1;
    }
}
