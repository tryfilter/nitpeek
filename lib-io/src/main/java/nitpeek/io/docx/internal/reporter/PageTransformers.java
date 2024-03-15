package nitpeek.io.docx.internal.reporter;

import nitpeek.io.docx.internal.common.SimpleDocxSegment;
import nitpeek.io.docx.types.DocxPage;
import nitpeek.io.docx.internal.pagesource.SimpleDocxPage;
import nitpeek.io.docx.types.CompositeRun;

import java.util.List;
import java.util.Map;

public final class PageTransformers {

    private PageTransformers() {
    }

    public static <C extends CompositeRun> DocxPage<C> keepOnlyBody(DocxPage<C> originalPage) {
        return new SimpleDocxPage<>(null, originalPage.getBody(), Map.of(), null);
    }

    public static <C extends CompositeRun> DocxPage<C> keepOnlyFootnotes(DocxPage<C> originalPage) {
        return new SimpleDocxPage<>(null, new SimpleDocxSegment<>(List.of()), originalPage.getFootnotes(), null);
    }
}
