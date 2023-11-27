package nitpeek.core.testutil;

import nitpeek.core.api.analyze.SimpleTextPage;
import nitpeek.core.api.analyze.TextPage;

import java.util.List;

public final class TestUtil {
    private TestUtil() {
    }

    public static TextPage emptyPage(int pageNumber) {
        return new SimpleTextPage(List.of(), pageNumber);
    }
}
