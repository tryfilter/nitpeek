package nitpeek.io.docx.internal.pagesource.render;

import nitpeek.io.docx.internal.common.DocxParagraph;
import nitpeek.io.docx.render.CompositeRun;

public interface ParagraphRenderer {
    String render(DocxParagraph<? extends CompositeRun> paragraph);
}
