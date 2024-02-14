package nitpeek.io.docx.internal.pagesource;

import java.util.List;

interface ContentRenderer {
    List<String> renderParagraphs(List<Object> paragraphs);
}
