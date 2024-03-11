package nitpeek.io.docx.internal.pagesource.run;

import nitpeek.io.docx.render.CompositeRun;
import org.docx4j.wml.R;

import java.util.List;

public final class ComplexRun implements CompositeRun {
    private final List<R> runs;

    public ComplexRun(List<R> runs) {
        this.runs = List.copyOf(runs);
    }

    @Override
    public List<R> componentRuns() {
        return runs;
    }
}