package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.internal.common.DocxUtil;
import nitpeek.io.docx.internal.pagesource.run.ComplexRun;
import nitpeek.io.docx.render.CompositeRun;
import nitpeek.io.docx.internal.pagesource.run.SingletonRun;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.R;
import org.docx4j.wml.STFldCharType;

import java.util.ArrayList;
import java.util.List;

/**
 * This {@code RunsCompositor} generates single-run composites for regular runs, but aggregates complex run sequences
 * into one composite per sequence
 */
final class ComplexRunsGrouper implements RunsCompositor {

    private final List<R> currentComplexRun = new ArrayList<>();

    @Override
    public List<CompositeRun> composit(List<R> runs) {
        var result = new ArrayList<CompositeRun>();
        for (R run : runs) {
            if (isInComplexField(run)) currentComplexRun.add(run);
            else result.add(new SingletonRun(run));

            if (isComplexFieldEnd(run)) {
                result.add(new ComplexRun(List.copyOf(currentComplexRun)));
                currentComplexRun.clear();
            }
        }
        return result;
    }

    private boolean isInComplexField(R run) {
        return isComplexFieldDelimiter(run) || !currentComplexRun.isEmpty();
    }

    private boolean isComplexFieldDelimiter(R run) {
        return isComplexFieldStart(run) || isComplexFieldEnd(run);
    }

    private boolean isComplexFieldStart(R run) {
        return isComplexFieldType(run, STFldCharType.BEGIN);
    }

    private boolean isComplexFieldEnd(R run) {
        return isComplexFieldType(run, STFldCharType.END);
    }

    private boolean isComplexFieldType(R run, STFldCharType type) {
        var controlRuns = DocxUtil.getElementValues(run, FldChar.class);
        if (controlRuns.isEmpty()) return false;
        FldChar control = controlRuns.getFirst();
        return control.getFldCharType() == type;
    }
}