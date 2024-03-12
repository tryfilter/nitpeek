package nitpeek.io.docx.internal.common;

import nitpeek.io.docx.render.CompositeRun;

public interface RunRenderer {
    String render(CompositeRun run);
}