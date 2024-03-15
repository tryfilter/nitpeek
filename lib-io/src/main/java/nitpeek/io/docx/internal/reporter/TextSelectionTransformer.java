package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.common.TextSelection;
import nitpeek.io.docx.types.CompositeRun;
import nitpeek.io.docx.internal.common.DocxTextSelection;

public interface TextSelectionTransformer<C extends CompositeRun> {
    DocxTextSelection<C> transform(TextSelection textSelection);
}