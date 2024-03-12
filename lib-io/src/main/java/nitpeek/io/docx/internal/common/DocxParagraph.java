package nitpeek.io.docx.internal.common;

import nitpeek.io.docx.render.CompositeRun;

import java.util.List;

public interface DocxParagraph<C extends CompositeRun> extends Partitioned<DocxParagraph<C>> {
    List<C> runs();
}
