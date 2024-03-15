package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.internal.common.RunRenderer;

import java.util.List;

final class EdgeDetector {
    public SelectionEdge computeSelectionEdge(List<? extends CompositeRun> runs, int offset, RunRenderer runRenderer) {

        int characterIndex = offset;
        for (int i = 0; i < runs.size(); i++) {
            var run = runs.get(i);
            int runLengthInCharacters = runRenderer.render(run).length();
            if (runLengthInCharacters > characterIndex) return new SelectionEdge(i, characterIndex);
            characterIndex -= runLengthInCharacters;
        }
        int lastIndex = runs.size() - 1;
        int remainingCharacters = runRenderer.render(runs.get(lastIndex)).length();
        throw new IndexOutOfBoundsException("Index " + characterIndex + " out of bounds for run at index " + lastIndex + " (run length is " + remainingCharacters + ")");
    }
    public record SelectionEdge(int runIndex, int characterIndexWithinRun) {
    }
}
