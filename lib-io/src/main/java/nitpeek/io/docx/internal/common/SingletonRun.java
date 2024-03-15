package nitpeek.io.docx.internal.common;

import nitpeek.io.docx.render.CompositeRun;
import org.docx4j.wml.R;

import java.util.List;
import java.util.Objects;

public final class SingletonRun implements CompositeRun {

    private final List<R> runs;

    public SingletonRun(R run) {
        this.runs = List.of(run);
    }

    @Override
    public List<R> componentRuns() {
        return runs;
    }

    /**
     * equals and hashCode are overridden so that annotations containing the same runs can split said runs while keeping
     * the splits synchronized between annotations.
     * More specifically, {@code SingletonRun}s are used as keys in a lookup table that is employed to reuse the
     * {@code SingletonRun}s when repeatedly converting the same one to a {@link nitpeek.io.docx.render.SplittableRun}.<br>
     * See also {@link nitpeek.io.docx.internal.reporter.DefaultRunSplitEnabler}.<br>
     * Normally, object identity would be general enough for equality conditions, EXCEPT when multiple pages refer to
     * the same set of {@link R} runs as is the case, for instance, with headers and footers. In such situations it can
     * be the case that the same runs are wrapped more than once into {@link SingletonRun}s which will have, in turn,
     * different object identities.
     */


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingletonRun that = (SingletonRun) o;
        return Objects.equals(runs, that.runs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(runs);
    }
}