package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.render.CompositeRun;
import org.docx4j.wml.R;

import java.util.List;

public interface RunsCompositor {
    List<CompositeRun> composit(List<R> runs);
}