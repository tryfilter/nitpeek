package com.nitpeek.io.docx.internal.common;

import com.nitpeek.io.docx.types.CompositeRun;

public interface RunRenderer {
    String render(CompositeRun run);
}