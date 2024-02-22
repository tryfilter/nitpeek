package nitpeek.io.docx.internal.pagesource;

import nitpeek.core.api.common.TextPage;

import java.util.List;

interface DocxPageRenderer {
    List<TextPage> renderPages(List<? extends DocxPage> pages);
}
