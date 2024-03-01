package nitpeek.io.docx.internal.pagesource;

import nitpeek.util.collection.ListEnds;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static nitpeek.io.docx.internal.pagesource.DocxUtil.*;

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

    public List<R> getRuns() {
        if (paragraphs().isEmpty()) return List.of();
        if (paragraphs().size() == 1)
            return new ArrayList<>(DocxUtil.getRuns(paragraphs().getFirst()).subList(indexOfFirstRun(), indexOfLastRun() + 1));

        var paragraphsSplit = new ListEnds<>(paragraphs());

        var firstParagraphRuns = DocxUtil.getRuns(paragraphsSplit.first());
        var runsForFirstParagraph = firstParagraphRuns.subList(indexOfFirstRun(), firstParagraphRuns.size());
        var runsForMiddleParagraphs = paragraphsSplit.middle().stream().map(DocxUtil::getRuns).flatMap(Collection::stream).toList();
        var lastParagraphRuns = DocxUtil.getRuns(paragraphsSplit.last());
        var runsForLastParagraph = lastParagraphRuns.subList(0, indexOfLastRun + 1);

        return Stream.of(runsForFirstParagraph, runsForMiddleParagraphs, runsForLastParagraph).flatMap(Collection::stream).toList();
    }
}
