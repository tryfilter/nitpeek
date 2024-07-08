package com.nitpeek.io.docx.internal.pagesource.render;

import com.nitpeek.io.docx.types.CompositeRun;
import com.nitpeek.io.docx.types.DocxPage;
import com.nitpeek.core.api.common.TextPage;

import java.util.List;

interface DocxPageRenderer {
    List<TextPage> renderPages(List<? extends DocxPage<? extends CompositeRun>> pages);
}
