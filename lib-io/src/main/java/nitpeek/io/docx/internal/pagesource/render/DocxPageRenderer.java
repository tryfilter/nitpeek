package nitpeek.io.docx.internal.pagesource.render;

import nitpeek.core.api.common.TextPage;
import nitpeek.io.docx.render.DocxPage;
import nitpeek.io.docx.render.CompositeRun;

import java.util.List;

interface DocxPageRenderer {
    List<TextPage> renderPages(List<? extends DocxPage<? extends CompositeRun>> pages);
}