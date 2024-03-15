package nitpeek.io.docx.types;

import org.docx4j.wml.R;

import java.util.List;

public interface CompositeRun {

    List<R> componentRuns();
}
