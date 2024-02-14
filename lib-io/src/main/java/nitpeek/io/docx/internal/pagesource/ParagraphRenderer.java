package nitpeek.io.docx.internal.pagesource;

import org.docx4j.wml.P;

interface ParagraphRenderer {
    String render(P paragraph);
}
