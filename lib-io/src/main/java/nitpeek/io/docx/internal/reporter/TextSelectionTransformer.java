package nitpeek.io.docx.internal.reporter;

import nitpeek.core.api.common.TextSelection;

interface TextSelectionTransformer {
    DocxTextSelection transform(TextSelection textSelection);
}