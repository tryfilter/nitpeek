package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.common.TextSelection;
import nitpeek.io.docx.render.CompositeRun;
import nitpeek.io.docx.render.DocxTextSelection;

public interface TextSelectionTransformer<C extends CompositeRun> {
    DocxTextSelection<C> transform(TextSelection textSelection);
}