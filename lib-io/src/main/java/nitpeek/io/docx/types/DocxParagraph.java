package nitpeek.io.docx.types;

import nitpeek.io.docx.internal.common.Partitioned;

import java.util.List;

public interface DocxParagraph<C extends CompositeRun> extends Partitioned<DocxParagraph<C>> {
    List<C> runs();
}
