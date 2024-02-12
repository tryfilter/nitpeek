package nitpeek.io.docx.internal;

import java.util.List;
import java.util.Set;

public record DocxPage(List<String> body, Set<Integer> footnotes) {

    public DocxPage {
        body = List.copyOf(body);
        footnotes = Set.copyOf(footnotes);
    }
}
