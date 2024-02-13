package nitpeek.io.docx.internal;

import java.util.List;

interface ContentRenderer {
    List<String> renderParagraphs(List<Object> paragraphs);
}
