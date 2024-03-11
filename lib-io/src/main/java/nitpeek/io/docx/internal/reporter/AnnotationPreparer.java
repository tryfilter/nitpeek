package nitpeek.io.docx.internal.reporter;


import nitpeek.io.docx.render.CompositeRun;
import nitpeek.io.docx.render.DocxTextSelection;
import nitpeek.io.docx.render.RenderableAnnotation;
import nitpeek.io.docx.render.SplittableRun;
import nitpeek.util.collection.ListEnds;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public final class AnnotationPreparer {

    /**
     * Makes the necessary changes for an annotation to be renderable.
     * This modifies the underlying DOCX structures in preparation for the rendering of the annotation but does not
     * modify the DOCX in a way that is visible to an end user.<br>
     * More specifically, individual runs may be split in order to ensure that the selection corresponding to the
     * annotation is delimited by run borders.
     */
    public RenderableAnnotation prepare(DocxAnnotation<? extends SplittableRun> annotation) {
        var selection = annotation.textSelection();
        return new RenderableAnnotation(annotation.message(), transformRuns(selection));
    }

    private List<CompositeRun> transformRuns(DocxTextSelection<? extends SplittableRun> selection) {
        var segment = selection.segment();
        var runs = segment.componentRuns();
        if (runs.isEmpty()) return List.of();
        if (runs.size() == 1) return List.of(runs.getFirst().splitBetween(selection.indexOfFirstCharacter(), selection.indexOfLastCharacter()));

        var runsSplit = new ListEnds<>(runs);
        var firstRun = runsSplit.first().splitFrom(selection.indexOfFirstCharacter());
        var middleRuns = runsSplit.middle();
        var lastRun = runsSplit.last().splitTo(selection.indexOfLastCharacter());

        return List.copyOf(Stream.of(List.of(firstRun), middleRuns, List.of(lastRun)).flatMap(Collection::stream).toList());
    }
}
