package com.nitpeek.io.docx.internal.reporter;

import com.nitpeek.core.api.common.TextSelection;
import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.internal.common.DocxTextSelection;

public interface TextSelectionTransformer<C extends CompositeRun> {
    DocxTextSelection<C> transform(TextSelection textSelection);
}