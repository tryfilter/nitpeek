package nitpeek.io.docx.internal.pagesource;

import java.util.List;
import java.util.Set;

interface FootnotesRenderer {
    List<String> renderFootnotes(Set<Integer> footnotesToRender);
}
