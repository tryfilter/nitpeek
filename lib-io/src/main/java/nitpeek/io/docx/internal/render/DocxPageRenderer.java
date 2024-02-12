package nitpeek.io.docx.internal.render;

import nitpeek.core.api.common.TextPage;
import nitpeek.io.docx.internal.DocxPage;

import java.util.List;

public interface DocxPageRenderer {
    List<TextPage> renderPages(List<DocxPage> pages);
}
