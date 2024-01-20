package nitpeek.core.testutil;

import nitpeek.core.api.analyze.SimpleTextPage;
import nitpeek.core.api.analyze.TextPage;
import nitpeek.core.api.common.*;

import java.util.Arrays;
import java.util.List;

public final class TestUtil {
    private TestUtil() {
    }

    public static TextPage emptyPage(int pageNumber) {
        return new SimpleTextPage(List.of(), pageNumber);
    }

    public static Feature featureFromComponents(SimpleFeatureType type, FeatureComponent... components) {
        return new SimpleFeature(type, Arrays.asList(components), 0);
    }
}
