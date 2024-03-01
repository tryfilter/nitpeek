package nitpeek.io.docx.internal.reporter;


import nitpeek.io.docx.internal.pagesource.DocxSegment;
import nitpeek.io.docx.internal.pagesource.DocxUtil;
import nitpeek.io.docx.render.DocxTextSelection;
import nitpeek.io.docx.render.RenderableAnnotation;
import nitpeek.util.collection.ListEnds;
import org.docx4j.XmlUtils;
import org.docx4j.wml.*;

import java.util.ArrayList;
import java.util.List;

public final class AnnotationPreparer {

    private final ObjectFactory objectFactory = new ObjectFactory();

    /**
     * Makes the necessary changes for an annotation to be renderable.
     * This modifies the underlying DOCX structures in preparation for the rendering of the annotation but does not
     * modify the DOCX in a way that is visible to an end user.<br>
     * More specifically, individual runs may be split in order to ensure that the selection corresponding to the
     * annotation is delimited by run borders.
     */
    public RenderableAnnotation prepare(DocxAnnotation annotation) {
        var selection = annotation.textSelection();
        if (selection.segments().size() == 1)
            return new RenderableAnnotation(annotation.message(), List.of(transformSingleSegment(selection.segments().getFirst(), selection)));

        var segmentSplit = new ListEnds<>(selection.segments());
        var resultSegments = new ArrayList<DocxSegment>();

        resultSegments.add(transformFirstSegment(segmentSplit.first(), selection));
        resultSegments.addAll(segmentSplit.middle());
        resultSegments.add(transformLastSegment(segmentSplit.last(), selection));

        return new RenderableAnnotation(annotation.message(), resultSegments);
    }

    private DocxSegment transformSingleSegment(DocxSegment segment, DocxTextSelection selection) {

        var adjustedSelectionLastSegment = adjustForSegmentSplit(segment, selection); // must go before the transformation as that can modify the underlying docx structures
        var segmentTransformedAsFirst = transformFirstSegment(segment, selection);
        
        return transformLastSegment(segmentTransformedAsFirst, adjustedSelectionLastSegment);
    }

    private DocxTextSelection adjustForSegmentSplit(DocxSegment segment, DocxTextSelection selection) {
        if (segment.getRuns().size() != 1) return selection;

        // If the segment only contained a single run, the effect of the split is relevant for further transformations.
        // The split has created a new run whose first character corresponds to the first character of the selection.
        // This effectively means that a run with text length equal to selection.indexOfFirstCharacter has been cut off
        // from the beginning of the segment. Therefore, the first character of the new run that belongs to the segment
        // is at index 0, and the last character's index is offset by the length of the cut-off run (selection.indexOfFirstCharacter)
        int firstCharacterIndex = 0;
        int lastCharacterIndex = selection.indexOfLastCharacter() - selection.indexOfFirstCharacter();

        return new DocxTextSelection(selection.segments(), firstCharacterIndex, lastCharacterIndex);
    }

    private DocxSegment transformFirstSegment(DocxSegment firstSegment, DocxTextSelection textSelection) {
        if (firstSegmentContainsNoPartialRuns(textSelection)) return firstSegment;

        var firstParagraph = firstSegment.paragraphs().getFirst();
        var runs = DocxUtil.getRuns(firstParagraph);
        splitAfter(textSelection.indexOfFirstCharacter(), runs, firstSegment.indexOfFirstRun());

        int newFirstRunIndex = firstSegment.indexOfFirstRun() + 1;
        int newLastRunIndex = postSplitLastRunIndex(firstSegment);
        return new DocxSegment(firstSegment.paragraphs(), newFirstRunIndex, newLastRunIndex);
    }

    private int postSplitLastRunIndex(DocxSegment firstSegment) {
        // If the segment only contains one paragraph, both indexOfFirstRun and indexOfLastRun are in reference to the
        // runs of said paragraph. In that case indexOfLastRun is also affected by inserting a new run while splitting
        if (firstSegment.paragraphs().size() == 1) return firstSegment.indexOfLastRun() + 1;
        else return firstSegment.indexOfLastRun();
    }

    private DocxSegment transformLastSegment(DocxSegment lastSegment, DocxTextSelection textSelection) {
        if (lastSegmentContainsNoPartialRuns(lastSegment, textSelection)) return lastSegment;

        var lastParagraph = lastSegment.paragraphs().getLast();
        var runs = DocxUtil.getRuns(lastParagraph);
        splitAfter(textSelection.indexOfLastCharacter() + 1, runs, lastSegment.indexOfLastRun());

        // No need to update the run indices since only indices after the last run of the segment have changed
        return lastSegment;
    }

    private void splitAfter(int firstCharacterInSecondRun, List<R> runs, int indexOfRunToSplit) {
        var runToSplit = runs.get(indexOfRunToSplit);
        String originalText = DocxUtil.getTextValue(runToSplit);
        if (originalText.length() <= 1) return; // nothing to split
        String firstRunText = originalText.substring(0, firstCharacterInSecondRun);
        String secondRunText = originalText.substring(firstCharacterInSecondRun);
        R runToInsert = clone(runToSplit);

        // apply the split of the text content
        var firstRunTextElement = DocxUtil.getText(runToSplit).orElse(null);
        setTextSpacePreserving(firstRunTextElement, firstRunText);

        var secondRunTextElement = objectFactory.createText();
        setTextSpacePreserving(secondRunTextElement, secondRunText);
        secondRunTextElement.setValue(secondRunText);
        runToInsert.getContent().add(objectFactory.createRT(secondRunTextElement));

        // inject the cloned run into the parent after the original run
        var parent = DocxUtil.getParent(runToSplit);
        parent.ifPresent(p -> p.getContent().add(indexOfRunToSplit + 1, runToInsert));
    }

    private void setTextSpacePreserving(Text text, String value) {
        if (text == null) return;

        text.setValue(value);
        text.setSpace("preserve");

    }

    private R clone(R run) {
        R clone = objectFactory.createR();
        var runProperties = run.getRPr();
        if (runProperties == null) return clone;
        clone.setRPr(cloneRunProperties(runProperties));
        return clone;
    }

    private RPr cloneRunProperties(RPr properties) {
        return XmlUtils.deepCopy(properties);
    }

    private boolean firstSegmentContainsNoPartialRuns(DocxTextSelection textSelection) {
        return textSelection.indexOfFirstCharacter() == 0;
    }

    private boolean lastSegmentContainsNoPartialRuns(DocxSegment segment, DocxTextSelection textSelection) {
        List<P> paragraphs = segment.paragraphs();
        if (paragraphs.isEmpty()) return true;
        List<R> runs = DocxUtil.getRuns(paragraphs.getLast());
        if (runs.isEmpty()) return true;
        R lastRun = runs.get(segment.indexOfLastRun());
        String lastRunText = DocxUtil.getTextValue(lastRun);

        return textSelection.indexOfLastCharacter() == lastRunText.length() - 1;
    }
}
