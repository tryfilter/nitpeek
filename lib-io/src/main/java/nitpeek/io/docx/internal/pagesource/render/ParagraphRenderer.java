package nitpeek.io.docx.internal.pagesource.render;

import nitpeek.io.docx.types.DocxParagraph;
import nitpeek.io.docx.types.CompositeRun;

public interface ParagraphRenderer {
    String render(DocxParagraph<? extends CompositeRun> paragraph);
}
