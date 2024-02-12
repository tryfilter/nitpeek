package nitpeek.io.docx.internal.render;

import java.util.List;
import java.util.Set;

public interface FootnotesRenderer {
    List<String> renderFootnotes(Set<Integer> footnotesToRender);
}
