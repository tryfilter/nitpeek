package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.common.TextSelection;
import nitpeek.io.docx.render.DocxTextSelection;

interface TextSelectionTransformer {
    DocxTextSelection transform(TextSelection textSelection);
}