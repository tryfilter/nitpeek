package nitpeek.io.docx;

import nitpeek.io.docx.internal.common.SimpleDocxSegment;
import nitpeek.io.docx.render.DocxPage;
import nitpeek.io.docx.internal.pagesource.SimpleDocxPage;
import nitpeek.io.docx.render.CompositeRun;

import java.util.List;
import java.util.Map;

public final class PageTransformers {

    public static DocxPage<CompositeRun> keepOnlyBody(DocxPage<CompositeRun> originalPage) {
        return new SimpleDocxPage<>(null, originalPage.getBody(), Map.of(), null);
    }

    public static DocxPage<CompositeRun> keepOnlyFootnotes(DocxPage<CompositeRun> originalPage) {
        return new SimpleDocxPage<>(null, new SimpleDocxSegment<>(List.of()), originalPage.getFootnotes(), null);
    }
}
