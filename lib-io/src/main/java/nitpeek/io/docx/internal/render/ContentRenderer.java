package nitpeek.io.docx.internal.render;

import java.util.List;

public interface ContentRenderer {
    List<String> renderParagraphs(List<Object> paragraphs);
}
