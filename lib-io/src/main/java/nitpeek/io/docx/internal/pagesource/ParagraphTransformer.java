package nitpeek.io.docx.internal.pagesource;

import nitpeek.io.docx.internal.common.DocxParagraph;
import nitpeek.io.docx.render.CompositeRun;
import org.docx4j.wml.P;

public interface ParagraphTransformer {
    DocxParagraph<CompositeRun> transform(P paragraph);

    DocxParagraph<CompositeRun> transformBetween(int firstRunIndex, int lastRunIndex, P paragraph);

    DocxParagraph<CompositeRun> transformFrom(int firstRunIndex, P paragraph);

    DocxParagraph<CompositeRun> transformTo(int lastRunIndex, P paragraph);
}
