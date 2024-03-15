package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.common.DocxUtil;
import nitpeek.io.docx.internal.common.RunRenderer;
import nitpeek.io.docx.internal.common.SingletonRun;
import nitpeek.io.docx.internal.reporter.EdgeDetector.SelectionEdge;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.types.SplittableRun;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.R;

import java.util.ArrayList;
import java.util.List;

final class SplitRun implements SplittableRun {
    private final List<R> runs;
    private final RunRenderer renderer;
    private final RunSplitter runSplitter;
    private final EdgeDetector edgeDetector = new EdgeDetector();


    public SplitRun(CompositeRun compositeRun, RunRenderer renderer, RunSplitter runSplitter) {
        this.runs = new ArrayList<>(compositeRun.componentRuns());
        this.renderer = renderer;
        this.runSplitter = runSplitter;
    }

    @Override
    public CompositeRun splitBetween(int firstIncludedCharacter, int lastIncludedCharacter) {
        splitAfter(firstIncludedCharacter - 1);
        splitAfter(lastIncludedCharacter);
        return new UpdatingRunList(firstIncludedCharacter, lastIncludedCharacter);
    }

    @Override
    public CompositeRun splitTo(int lastIncludedCharacter) {
        return splitBetween(0, lastIncludedCharacter);
    }

    @Override public CompositeRun splitFrom(int firstIncludedCharacter) {
        return splitBetween(firstIncludedCharacter, renderer.render(this).length() - 1);
    }

    private void splitAfter(int splitIndex) {
        var compositeRuns = runsWrapped();
        var edge = edgeDetector.computeSelectionEdge(compositeRuns, splitIndex, renderer);
        var selectedRun = runs.get(edge.runIndex());
        if (isOnRunBorder(edge, selectedRun)) return; // nothing to split: split index is on run border
        var splitResult = runSplitter.splitAfter(selectedRun, edge.characterIndexWithinRun());
        splitResult.splitOff().ifPresent(r -> addNewRunAt(edge.runIndex() + 1, r, selectedRun));
    }

    private List<CompositeRun> runsWrapped() {
        return List.copyOf(runs.stream().map(SingletonRun::new).toList());
    }

    private boolean isOnRunBorder(SelectionEdge edge, R run) {
        int runLength = renderer.render(new SingletonRun(run)).length();
        return edge.characterIndexWithinRun() < 0 || edge.characterIndexWithinRun() >= runLength - 1;
    }

    private void addNewRunAt(int runIndex, R newRun, R originalRun) {
        runs.add(runIndex, newRun);
        var parentParagraph = DocxUtil.getParent(originalRun);
        parentParagraph.ifPresent(p -> addNewRunAfter(originalRun, newRun, p));
    }

    private void addNewRunAfter(R originalRun, R newRun, ContentAccessor parent) {
        var siblings = parent.getContent();
        int newRunIndex = siblings.indexOf(originalRun) + 1;
        siblings.add(newRunIndex, newRun);
    }

    private final class UpdatingRunList implements CompositeRun {

        private final int firstIncludedCharacter;
        private final int lastIncludedCharacter;

        private UpdatingRunList(int firstIncludedCharacter, int lastIncludedCharacter) {
            this.firstIncludedCharacter = firstIncludedCharacter;
            this.lastIncludedCharacter = lastIncludedCharacter;
        }

        @Override
        public List<R> componentRuns() {
            var wrappedRuns = runsWrapped();
            var startEdge = edgeDetector.computeSelectionEdge(wrappedRuns, firstIncludedCharacter, renderer);
            var endEdge = edgeDetector.computeSelectionEdge(wrappedRuns, lastIncludedCharacter, renderer);
            return List.copyOf(runs.subList(startEdge.runIndex(), endEdge.runIndex() + 1));
        }
    }

    @Override
    public List<R> componentRuns() {
        return List.copyOf(runs);
    }
}