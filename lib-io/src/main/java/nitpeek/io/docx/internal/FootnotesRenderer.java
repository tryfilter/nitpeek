package nitpeek.io.docx.internal;

import java.util.List;
import java.util.Set;

interface FootnotesRenderer {
    List<String> renderFootnotes(Set<Integer> footnotesToRender);
}
