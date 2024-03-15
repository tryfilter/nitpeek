package nitpeek.io.docx.internal.common;

import nitpeek.io.docx.types.CompositeRun;

public interface RunRenderer {
    String render(CompositeRun run);
}