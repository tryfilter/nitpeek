package nitpeek.io.docx.internal;

import org.docx4j.wml.P;

interface ParagraphRenderer {
    String render(P paragraph);
}
