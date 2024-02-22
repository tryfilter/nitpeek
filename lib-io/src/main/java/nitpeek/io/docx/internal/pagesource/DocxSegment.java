package nitpeek.io.docx.internal.pagesource;

import org.docx4j.wml.P;

import java.util.List;

import static nitpeek.io.docx.internal.pagesource.DocxUtil.*;

/**
 * Represents a segment of contiguous text, with the extents being defined at the "run" granularity level.
 *
 * @param paragraphs      the P elements whose contents constitute this segment
 * @param indexOfFirstRun defines for the first paragraph in {@link #paragraphs} the first run that is part of this segments
 * @param indexOfLastRun  defines for the last paragraph in {@link #paragraphs} the last run that is part of this segments
 */
public record DocxSegment(List<P> paragraphs, int indexOfFirstRun, int indexOfLastRun) {
    public DocxSegment {
        paragraphs = List.copyOf(paragraphs);
    }

    public DocxSegment(List<P> paragraphs) {
        this(paragraphs, 0, getIndexOfLastRun(paragraphs));
    }
}
