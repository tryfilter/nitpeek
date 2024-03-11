package nitpeek.io.docx.internal.pagesource.run;

import nitpeek.io.docx.render.CompositeRun;
import nitpeek.io.docx.render.SplittableRun;
import org.docx4j.wml.R;

import java.util.List;

public final class AtomicRun implements SplittableRun {

    private final List<R> runs;

    public AtomicRun(CompositeRun compositeRun) {
        this.runs = List.copyOf(compositeRun.componentRuns());
    }

    /**
     * @return an unmodifiable copy
     */
    @Override
    public List<R> componentRuns() {
        return runs;
    }

    // Atomic run is not splittable: simply return itself
    @Override
    public CompositeRun splitBetween(int firstIncludedCharacter, int lastIncludedCharacter) {
        return this;
    }

    @Override
    public CompositeRun splitFrom(int firstIncludedCharacter) {
        return this;
    }

    @Override
    public CompositeRun splitTo(int lastIncludedCharacter) {
        return this;
    }
}
